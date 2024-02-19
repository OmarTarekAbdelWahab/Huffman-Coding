import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class WeightedActivitySelection {
    public static int solve(int[][] arr, int n){
        Arrays.sort(arr, (o1, o2) -> {
            if(o1[1] > o2[1]){
                return 1;
            }else if(o1[1] == o2[1]){
                return 0;
            }
            return -1;
        });
        int[] lastCompatible = new int[n];
        Arrays.fill(lastCompatible, -1);
        for(int i = 1; i < n; i++){
            int start = arr[i][0];
            int l = 0, r = n;
            while(r > l + 1){
                int m = (r+l)/2;
                if(arr[m][1] <= start){
                    l = m;
                }else{
                    r = m;
                }
            }
            if(arr[l][1] <= start){
                lastCompatible[i] = l;
            }
        }
        int[]dp = new int[n];
        dp[0] = arr[0][2];
        for(int i = 1; i < n; i++){
            if(lastCompatible[i] == -1){
                dp[i] = Math.max(dp[i-1], arr[i][2]);
            }else{
                dp[i] = Math.max(dp[i-1], arr[i][2] + dp[lastCompatible[i]]);
            }
        }
        return Arrays.stream(dp).max().getAsInt();
    }

    public static void main(String[] args) {
        String filename;
        File file;
        int n;
        int [][] arr;
        try {
            filename = args[0];
            file = new File(args[0]);
            Scanner sc = new Scanner(file);
            n = sc.nextInt();
            arr = new int[n][3];
            for (int i = 0; i < n; i++) {
                arr[i][0] = sc.nextInt();
                arr[i][1] = sc.nextInt();
                arr[i][2] = sc.nextInt();
            }
        }catch (Exception e){
            System.out.println("Error occurred with the entered file!!");
            return;
        }
        try {
            File outputFile = new File(filename.substring(0, filename.lastIndexOf('.')) + "_20010998.out");
            outputFile.createNewFile();
            FileWriter fileWriter = new FileWriter(outputFile);
            int res = solve(arr, n);
            fileWriter.write(Integer.toString(res));
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file!!.");
        }
    }
}