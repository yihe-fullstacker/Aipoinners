public class Test {

    public static void main(String[] args) {

        int[] arr = {5,5,5,6,4,5};

        Test t = new Test();

        System.out.println(t.minNumberInRotateArray(arr));

    }

    public int minNumberInRotateArray(int[] array) {

        if(array == null || array.length == 0){
            return 0;
        }

        int left = 0;
        int right = array.length - 1;

        while(left < right){

            int mid = (left + right) / 2;

            if(array[mid] > array[right]){

                left = mid + 1;

            }else if(array[mid] < array[right]){

                right = mid;

            }else{

                // array[mid] == array[right]
                right--;

            }

        }

        return array[left];
    }
}