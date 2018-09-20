package com.jd.xn.clinet.rpc;

/**
 * 可时候这的定时器
 *
 * @author lijizhen1@jd.com
 * @date 2018/4/14 11:39
 */
public class ResetableTimer {
    private volatile boolean running;
    private Thread boss;
    private Runnable task;
    private int period;
    protected long lastTime;
    private String name;


    public ResetableTimer(int periodMillisecond) {
        this(periodMillisecond, null);
    }

    public ResetableTimer(int periodMillisecond, Runnable task) {
        this(periodMillisecond, task, null);
    }

    public ResetableTimer(int periodMillisecond, Runnable task, String name) {
        this.period = periodMillisecond;
        delay(0 - this.period);
        setTask(task);
        this.name = name;
    }

    public void setTask(Runnable task) {
        this.task = task;
    }

    public void start() {
        if (this.boss != null) {
            return;
        }
        this.running = true;
        /**
         * 创建主线程
         */
        this.boss = new Thread(new Runnable() {
            @Override
            public void run() {
                while (ResetableTimer.this.running) {
                    long split = System.currentTimeMillis() - ResetableTimer.this.lastTime;
                    if ((split >= ResetableTimer.this.period) && (ResetableTimer.this.task != null)) {
                        try {
                            ResetableTimer.this.task.run();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ResetableTimer.this.delay();
                    }
                    try {
                        Thread.sleep(split >= ResetableTimer.this.period ? ResetableTimer.this.period : ResetableTimer.this.period - split);
                    } catch (InterruptedException e) {
                        ResetableTimer.this.running = false;
                    }
                }
            }
        });
        if (this.name != null) {
            this.boss.setName(this.name);
        }
        this.boss.setDaemon(true);
        this.boss.start();
    }

    public void stop() {
        this.running = false;
        this.boss.interrupt();
        this.boss = null;
    }

    public void delay() {
        delay(0);
    }

    public void delay(int delayMillisecond) {
        this.lastTime = (System.currentTimeMillis() + delayMillisecond);
    }
}
