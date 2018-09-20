package com.jd.xn.clinet;

import com.google.common.collect.Sets;
import com.jd.xn.clinet.exceptions.IdIllgealException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author lijizhen1@jd.com
 * @date 2018/8/4 10:57
 */
public final class CloudId {
    /**
     * 加载集群ID值的加载因子
     */
    static final float _threshold = 0.5F;
    static volatile int state = -1;

    private final static int LOADING = 1;
    private final static int LOADED = 2;

    volatile static boolean preLoadFinish = false;

    /**
     * 这里对外提供的唯一序列ID
     *
     * @return
     */
    public static long getCloudId() {
        CloudIdContainer cloudIdContainer = getSingleton();
        return cloudIdContainer.getNode().getId();
    }


    private static volatile CloudIdContainer singleton = null;

    /**
     * 获取对应的值
     *
     * @return
     */
    public static CloudIdContainer getSingleton() {
        if (singleton != null) {
            return singleton;
        } else {
            //这里开始时候会执行到这里多个线程在这里阻塞，后边来的线程就不会阻塞了
            synchronized (CloudIdContainer.class) {
                if (singleton == null) {
                    singleton = new CloudIdContainer(100);
                }
            }
        }
        return singleton;
    }


    /**
     * 同步数据
     */
    static class CloudIdContainer {
        Lock lock = new ReentrantLock();
        /**
         * 存放执行Node的map
         */
        private final Map<String, Node> nodeMap = new HashMap<>(2);

        private String[] currentKey;
        /**
         * 当前正在使用下标地址
         */
        private volatile int currentIndex = 0;
        /**
         * 最大加载NODE的容器数据默认是2
         */
        private final int loadSize = 2;

        private int step;//用于动态的调整请求的跨度
        private int threshold; //加载的阀值


        public CloudIdContainer(int stemp) {
            //增加系统探针
            currentKey = new String[loadSize];
            for (int i = 0; i < loadSize; i++) {
                currentKey[i] = String.valueOf(i);
                currentIndex = i;
            }
            this.step = stemp;
            threshold = (int) (_threshold * step);
        }


        /**
         * 计算获得生成并得到当前生产的节点
         *
         * @return
         */
        public Node getNode() {
            lock.lock();
            Node currentNode = nodeMap.get(currentKey[currentIndex]);
            IdIllgealException ee = null;
            //暂时设置首次加载时候时候调用返回，看压测结果后期修改是否需要预加载
            try {
                //对象中还有值需要判断是否还可以产生
                if (null != currentNode) {
                    if (!currentNode.isEmpty()) {
                        //是否超过阀值,
                        if (currentNode.isUpLoad(threshold)) {
                            //触及到阈值可以预先加载
                            if (!preLoadFinish &&
                                    nodeMap.size() < loadSize) {
                                remoteLoad();
                                //预加载完成
                                preLoadFinish = true;
                            }
                        }
                        return currentNode;
                    }
                    if (currentNode.isEmpty()) {
                        //重置为空
                        nodeMap.remove(currentKey[currentIndex]);
                        //移动下标指向
                        currentIndex = moveIndex();
                        //重置可以预加载
                        preLoadFinish = false;
                    }
                }
                //首次执行 节点首次进来时候为空
                if (null == currentNode
                        && state < LOADING) {
                    remoteLoad();
                    currentIndex = moveIndex();
                }
                return getNode();
            } catch (Exception e) {
                e.printStackTrace();
                ee = new IdIllgealException(e);
            } finally {
                lock.unlock();
            }
            if (null != ee) {
                throw ee;
            }
            throw new IdIllgealException("执行片数据时候失败");
        }


        private static volatile long start = 1;

        /**
         * 生产对应的
         */
        private void remoteLoad() throws ExecutionException {
            state = LOADING;
            ExecutorService executorService = Executors.newFixedThreadPool(1);
            try {
                Future<Boolean> future = executorService.submit(() -> {
                    //这里加载
                    int movedIndex = moveIndex();
                    //预先加载对应值，TODO 这里需要调用远程的请求Handler
                    nodeMap.put(currentKey[movedIndex], new Node(start, start + step - 1));
                    //System.out.println("当前移动的下标地址" + movedIndex + "移动后存放的开始值" + start);
                    start = start + step;
                    return true;
                });
                if (future.get()) {
                    //加载状态设置完成
                    state = LOADED;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new IdIllgealException(e);
            } finally {
                executorService.shutdown();
            }
        }

        /**
         * 移动游标
         *
         * @return
         */
        int moveIndex() {
            int _c = currentIndex;
            //下标值如果为越界重置
            if (_c == loadSize - 1) {
                _c = 0;
            } else {
                ++_c;
            }
            return _c;
        }

    }


    public static void main(String[] args) {

        //测试生产数据
        testProducer();
        //计算生产的时间
        //produce();
    }


    static void testProducer() {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 300; i++) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    System.out.println(CloudId.getCloudId());
                }
            });
        }
        executorService.shutdown();

    }

    static void produce() {
        Set<Long> set = Sets.newHashSet();
        Long start = System.currentTimeMillis();
        for (int j = 0; j < 1000000; j++) {
            set.add(CloudId.getCloudId());
        }
        Long end = System.currentTimeMillis();
        System.out.println("CloudId产生" + set.size() + "个需要时间" + (end - start) + "ms:" + "平均速率" + Double.valueOf(set.size()) / (end - start) + "个/秒");
    }
}


/**
 * 承载生成唯一ID的值
 */
class Node {
    private static volatile long current = -1;
    /**
     * 开始范围
     */
    private final long start;
    /**
     * 截止范围
     */
    private final long end;


    public Node(long start, long end) {
        this.start = start;
        this.end = end;
        //为了后续的都执行++ 操作
        current = start - 1;
    }

    /**
     * 判断是否为空，等待回收
     */
    boolean isEmpty() {
        return current == end;
    }

    /**
     * 计算是否超过了阀值
     */
    boolean isUpLoad(long threshold) {
        if (current + threshold > end) {
            return true;
        }
        return false;
    }

    /**
     * 获得ID值
     *
     * @return
     */
    public synchronized long getId() {
        if (current == -1L) {
            throw new IdIllgealException("检查当前获取的current是否正确");
        }
        return ++current;
    }

}