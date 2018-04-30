package compare;

import java.util.Arrays;

/**
 * 插入排序
 * 在已排序序列(左手已经排好序的手牌)中从后向前扫描，找到相应位置并插入。
 *
 * @author lijizhen1@jd.com
 * @date 2018/4/30 15:22
 */
public class InsertCompare {

    /**
     * 从第一个元素开始，该元素可以认为已经被排序
     * 取出下一个元素，在已经排序的元素序列中从后向前扫描
     * 如果该元素（已排序）大于新元素，将该元素移到下一位置
     * 重复步骤3，直到找到已排序的元素小于或者等于新元素的位置
     * 将新元素插入到该位置后
     * 重复步骤2~5
     *
     * @param A
     * @param n
     */
    public static void insertionSort(int A[], int n) {
        for (int i = 1; i < n; i++)         // 类似抓扑克牌排序
        {
            int get = A[i];                 // 右手抓到一张扑克牌
            int j = i - 1;                  // 拿在左手上的牌总是排序好的
            while (j >= 0 && A[j] > get)    // 将抓到的牌与手牌从右向左进行比较
            {
                A[j + 1] = A[j];            // 如果该手牌比抓到的牌大，就将其右移
                j--;
            }
            A[j + 1] = get; // 直到该手牌比抓到的牌小(或二者相等)，将抓到的牌插入到该手牌右边(相等元素的相对次序未变，所以插入排序是稳定的)
        }
    }
// 分类 ------------- 内部比较排序
// 数据结构 ---------- 数组
// 最差时间复杂度 ---- 最坏情况为输入序列是降序排列的,此时时间复杂度O(n^2)
// 最优时间复杂度 ---- 最好情况为输入序列是升序排列的,此时时间复杂度O(n)
// 平均时间复杂度 ---- O(n^2)
// 所需辅助空间 ------ O(1)
// 稳定性 ------------ 稳定


    /**
     * 　当n较大时，二分插入排序的比较次数比直接插入排序的最差情况好得多，但比直接插入排序的最好情况要差，
     * 所当以元素初始序列已经接近升序时，直接插入排序比二分插入排序比较次数少。二分插入排序元素移动次数与直接插入排序相同，依赖于元素初始序列。
     *
     * @param A
     * @param n
     */
    public static void insertionSortDichotomy(int A[], int n) {
        for (int i = 1; i < n; i++) {
            int get = A[i];                    // 右手抓到一张扑克牌
            int left = 0;                    // 拿在左手上的牌总是排序好的，所以可以用二分法
            int right = i - 1;                // 手牌左右边界进行初始化
            while (left <= right)            // 采用二分法定位新牌的位置
            {
                int mid = (left + right) / 2;
                if (A[mid] > get)
                    right = mid - 1;
                else
                    left = mid + 1;
            }
            for (int j = i - 1; j >= left; j--)    // 将欲插入新牌位置右边的牌整体向右移动一个单位
            {
                A[j + 1] = A[j];
            }
            A[left] = get;                    // 将抓到的牌插入手牌
        }
    }

    /**
     * 希尔排序，也叫递减增量排序，是插入排序的一种更高效的改进版本。希尔排序是不稳定的排序算法。
     *希尔排序通过将比较的全部元素分为几个区域来提升插入排序的性能。这样可以让一个元素可以一次性地朝最终位置前进一大步。然后算法再取越来越小的步长进行排序，算法的最后一步就是普通的插入排序，但是到了这步，需排序的数据几乎是已排好的了（此时插入排序较快）。
     　　假设有一个很小的数据在一个已按升序排好序的数组的末端。如果用复杂度为O(n^2)的排序（冒泡排序或直接插入排序），可能会进行n次的比较和交换才能将该数据移至正确位置。而希尔排序会用较大的步长移动数据，所以小数据只需进行少数比较和交换即可到正确位置。
     * @param A
     * @param n
     */
    public static void ShellSort(int A[], int n) {
        int h = 0;
        while (h <= n)                          // 生成初始增量
        {
            h = 3 * h + 1;
        }
        while (h >= 1) {
            for (int i = h; i < n; i++) {
                int j = i - h;
                int get = A[i];
                while (j >= 0 && A[j] > get) {
                    A[j + h] = A[j];
                    j = j - h;
                }
                A[j + h] = get;
            }
            h = (h - 1) / 3;                    // 递减增量
        }
    }

    public static void main(String[] args) {
        int A[] = {6, 5, 3, 1, 8, 7, 2, 4};// 从小到大插入排序
        System.out.println("插入排序结果：");
        insertionSort(A, A.length);
        System.out.println(Arrays.toString(A));


        int As[] = {6, 5, 3, 1, 8, 7, 2, 4};// 从小到大插入排序
        System.out.println("二分插入排序结果：");
        insertionSortDichotomy(As, As.length);
        System.out.println(Arrays.toString(As));



        int Ah[] = { 5, 2, 9, 4, 7, 6, 1, 3, 8 };// 从小到大希尔排序
        System.out.println("插入希尔排序结果：");
        insertionSortDichotomy(Ah, Ah.length);
        System.out.println(Arrays.toString(Ah));
    }
}
