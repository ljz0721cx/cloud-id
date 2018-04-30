package compare;

import java.util.Arrays;

/**
 * 选择排序
 * 初始时在序列中找到最小（大）元素，放到序列的起始位置作为已排序序列；然后，再从剩余未排序元素中继续寻找最小（大）元素，放到已排序序列的末尾。以此类推，直到所有元素均排序完毕。
 *
 * @author lijizhen1@jd.com
 * @date 2018/4/30 15:22
 */
public class SelectCompare {
    public static void Swap(int A[], int i, int j) {
        int temp = A[i];
        A[i] = A[j];
        A[j] = temp;
    }

    /**
     * 冒泡排序通过依次交换相邻两个顺序不合法的元素位置，从而将当前最小（大）元素放到合适的位置；而选择排序每遍历一次都记住了当前最小（大）元素的位置，最后仅需一次交换操作即可将其放到合适的位置。
     *
     * @param A
     * @param n
     */
    public static void selectionSort(int A[], int n) {
        for (int i = 0; i < n - 1; i++)         // i为已排序序列的末尾
        {
            int min = i;
            for (int j = i + 1; j < n; j++)     // 未排序序列
            {
                if (A[j] < A[min])              // 找出未排序序列中的最小值
                {
                    min = j;
                }
            }
            if (min != i) {
                Swap(A, min, i);    // 放到已排序序列的末尾，该操作很有可能把稳定性打乱，所以选择排序是不稳定的排序算法
                System.out.println(Arrays.toString(A));
            }
        }
    }

    public static void main(String[] args) {
        int A[] = {8, 5, 2, 6, 9, 3, 3, 1, 4, 0, 7}; // 从小到大选择排序
        System.out.println("选择排序结果,依次输出为：");
        selectionSort(A, A.length);
    }
}
