package compare;

import java.util.Arrays;

/**
 * 冒泡排序 O(n)-O(n^2) O(l) 稳定
 * 冒泡排序算法的运作如下：
 * <p>
 * 1.比较相邻的元素，如果前一个比后一个大，就把它们两个调换位置。
 * 2.对每一对相邻元素作同样的工作，从开始第一对到结尾的最后一对。这步做完后，最后的元素会是最大的数。
 * 3.针对所有的元素重复以上的步骤，除了最后一个。
 * 4.持续每次对越来越少的元素重复上面的步骤，直到没有任何一对数字需要比较。
 *
 * @author lijizhen1@jd.com
 * @date 2018/4/30 15:21
 */
public class BubblingCompare {

    public static void Swap(int A[], int i, int j) {
        int temp = A[i];
        A[i] = A[j];
        A[j] = temp;
    }

    /**
     * 简单冒泡排序
     *
     * @param A
     * @param n
     */
    public static void BubbleSort(int A[], int n) {
        for (int j = 0; j < n - 1; j++)         // 每次最大元素就像气泡一样"浮"到数组的最后
        {
            for (int i = 0; i < n - 1 - j; i++) // 依次比较相邻的两个元素,使较大的那个向后移
            {
                if (A[i] > A[i + 1])            // 如果条件改成A[i] >= A[i + 1],则变为不稳定的排序算法
                {
                    Swap(A, i, i + 1);
                }
            }
        }
    }


    /**
     * 鸡尾酒冒泡排序
     * 此算法与冒泡排序的不同处在于从低到高然后从高到低，而冒泡排序则仅从低到高去比较序列里的每个元素。他可以得到比冒泡排序稍微好一点的效能。
     * 以序列(2,3,4,5,1)为例，鸡尾酒排序只需要访问一次序列就可以完成排序，但如果使用冒泡排序则需要四次。但是在乱数序列的状态下，鸡尾酒排序与冒泡排序的效率都很差劲。
     * @param A
     * @param n
     */
    public static void cocktailSort(int A[], int n) {
        int left = 0;                            // 初始化边界
        int right = n - 1;
        while (left < right) {
            for (int i = left; i < right; i++)   // 前半轮,将最大元素放到后面
            {
                if (A[i] > A[i + 1]) {
                    Swap(A, i, i + 1);
                }
            }
            right--;
            for (int i = right; i > left; i--)   // 后半轮,将最小元素放到前面
            {
                if (A[i - 1] > A[i]) {
                    Swap(A, i - 1, i);
                }
            }
            left++;
        }
    }



    public static void main(String[] args) {
        int A[] = {6, 5, 3, 1, 8, 7, 2, 4};    // 从小到大冒泡排序

        BubbleSort(A, A.length);
        System.out.println("普通冒泡排序结果：");
        System.out.println(Arrays.toString(A));


        int As[] = {6, 5, 3, 1, 8, 7, 2, 4};    // 从小到大冒泡排序
        cocktailSort(As,As.length);
        System.out.println("鸡尾酒冒泡排序结果：");
        System.out.println(Arrays.toString(As));
    }
}
