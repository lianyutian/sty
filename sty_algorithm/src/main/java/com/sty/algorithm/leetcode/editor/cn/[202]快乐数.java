import java.util.HashSet;
import java.util.Set;

//leetcode submit region begin(Prohibit modification and deletion)
class Solution {

    public static void main(String[] args) {
        isHappy(3);
    }

    public static boolean isHappy(int n) {
        Set<Integer> set = new HashSet<>();
        int sum = 0;
        while(sum != 1 || n != 0) {
            int i  = (n % 10) * (n % 10);
            sum += i;
            n = n / 10;

            if (n == 0) {
                if (sum == 1) {
                    break;
                }
                n = sum;
                if (set.contains(sum)) {
                    return false;
                }
                set.add(sum);
                sum = 0;
            }
        }

        return true;

    }



}
//leetcode submit region end(Prohibit modification and deletion)
