import java.io.*;
import java.util.*;

class Node implements Comparable<Node>{
    Node left;
    Node right;
    byte[] value;
    int frequency;
    boolean isLeaf;

    Node(){}
    Node mergeNodes(Node secondNode){
        Node mergedNode = new Node();
        mergedNode.isLeaf = false;
        mergedNode.frequency = this.frequency + secondNode.frequency;
        mergedNode.left = this;
        mergedNode.right = secondNode;
        mergedNode.value = null;
        return mergedNode;
    }
    Node(byte[] bytes, int frequency){
        this.value = bytes;
        this.frequency = frequency;
        this.isLeaf = true;
    }

    @Override
    public int compareTo(Node o) {
        if(this.frequency > o.frequency){
            return 1;
        }else if(this.frequency == o.frequency){
            return 0;
        }
        return -1;
    }

}

final class ByteArrayWrapper
{
    private final byte[] bytes;

    public ByteArrayWrapper(byte[] bytes)
    {
        if (bytes == null)
        {
            throw new NullPointerException();
        }
        this.bytes = bytes;
    }
    public byte[] getBytes(){
            return bytes;
    }


    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof ByteArrayWrapper))
        {
            return false;
        }
        return Arrays.equals(bytes, ((ByteArrayWrapper)other).bytes);
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(bytes);
    }
    @Override
    public String toString(){
        if(this.bytes.length==0) return "[]";
        String s = "[" + bytes[0];
        for (int i = 1; i < bytes.length; i++){
            s += ", " + bytes[i];
        }
        s+="]";
        return s;
    }
}

public class Huffman {

    public static Map<ByteArrayWrapper, String> encode(Map<ByteArrayWrapper, Integer> frequencyMap){
        if(frequencyMap.size()==1){
            Map<ByteArrayWrapper, String> encodeMap = new HashMap<>();
            encodeMap.put(frequencyMap.entrySet().iterator().next().getKey(), "0");
            return encodeMap;
        }
        Node root = buildTree(frequencyMap);
        Map<ByteArrayWrapper, String> encodeMap = new HashMap<>();
        buildCode(root, "", encodeMap);
        return encodeMap;
    }
    public static Node buildTree(Map<ByteArrayWrapper, Integer> frequencyMap){
        Queue<Node> queue = new PriorityQueue<>();
        // add the leaves
        for (var entry  :
                frequencyMap.entrySet()) {
            queue.add(new Node(entry.getKey().getBytes(), entry.getValue()));
        }
        while(queue.size() > 1){
            Node node1 = queue.poll();
            Node node2 = queue.poll();
            Node mergedNode = node1.mergeNodes(node2);
            queue.add(mergedNode);
        }
        return queue.peek();
    }
    public static void buildCode(Node root, String code, Map<ByteArrayWrapper, String> codeMap){
        if(root.isLeaf){
            codeMap.put(new ByteArrayWrapper(root.value), code);
        }else{
            buildCode(root.left, code + "0", codeMap);
            buildCode(root.right, code + "1", codeMap);
        }
    }
    public static void readFile(String filename, int n, Map<ByteArrayWrapper, Integer> frequencyMap) throws IOException {
        final int SIZE = 100_000_000 - 100_000_000%n;
        byte buffer[] = new byte[SIZE];
        FileInputStream data = new FileInputStream(filename);
        int available = data.available();
        long remaining = available;
        for (long i = 0; i < available/SIZE;i++) {
            data.read(buffer);
            System.out.println("Adding..");
            addToMap(frequencyMap, buffer, n);
            System.out.println("Added!!");
            remaining-=SIZE;
        }
        // read left over
        byte remBuffer[] = new byte[(int)remaining];
        data.read(remBuffer);
        addToMapRemaining(frequencyMap, remBuffer, n);
    }

    private static void addToMapRemaining(Map<ByteArrayWrapper, Integer> frequencyMap, byte[] remBuffer, int n) {
        int i = 0;
        while(i+n <= remBuffer.length){
            byte bytes[] = new byte[n];
            for (int iind = i; iind < i + n; iind++){
                bytes[iind-i] = remBuffer[iind];;
            }
            ByteArrayWrapper byteArrayWrapper = new ByteArrayWrapper(bytes);


            if(frequencyMap.containsKey(byteArrayWrapper)){
                frequencyMap.put(byteArrayWrapper, frequencyMap.get(byteArrayWrapper)+1);
            }else{
                frequencyMap.put(byteArrayWrapper, 1);
            }
            i=i+n;
        }
        if(i < remBuffer.length){

            byte bytes[] = new byte[remBuffer.length-i+1];
            for (int iind = i; iind < remBuffer.length; iind++){
                bytes[iind-i] = remBuffer[iind];;
            }
            ByteArrayWrapper byteArrayWrapper = new ByteArrayWrapper(bytes);

            if(frequencyMap.containsKey(byteArrayWrapper)){
                frequencyMap.put(byteArrayWrapper, frequencyMap.get(byteArrayWrapper)+1);
            }else{
                frequencyMap.put(byteArrayWrapper, 1);
            }
        }
    }

    public static void addToMap(Map<ByteArrayWrapper, Integer> frequencyMap, byte[] buffer, int n){
        int i = 0;
        while(i+n <= buffer.length){
            byte bytes[] = new byte[n];
            for (int iind = i; iind < i + n; iind++){
                bytes[iind-i] = buffer[iind];;
            }
            ByteArrayWrapper byteArrayWrapper = new ByteArrayWrapper(bytes);
            if(frequencyMap.containsKey(byteArrayWrapper)){
                frequencyMap.put(byteArrayWrapper, frequencyMap.get(byteArrayWrapper)+1);
            }else{
                frequencyMap.put(byteArrayWrapper, 1);
            }
            i=i+n;
        }
    }





    public static void  encodeInFile(String filename, String newFilename, Map<ByteArrayWrapper, String> codeMap, int n) throws IOException {
//        RandomAccessFile data = new RandomAccessFile(System.getProperty("user.dir")+"/src/"+filename, "r");
        FileInputStream data = new FileInputStream(filename);
        System.out.println("File Size: "+data.available());
        FileOutputStream writeFile = new FileOutputStream(newFilename);

        final int SIZE = 100_000_000 - 100_000_000%n;
        byte buffer[] = new byte[SIZE];
        int available = data.available();
        long remaining = available;
        int i = 7;
        byte b = 0;
        byte bArr[] = new byte[SIZE];
        int bInd = 0;

        System.out.println("Map size: "+ codeMap.size());
        DataOutputStream ds = new DataOutputStream(writeFile);
        // write the size of the map
        ds.writeInt(codeMap.size());
        byte keySizes[] = new byte[codeMap.size()];
        int byteInd = 0;
        byte valueSizes[] = new byte[codeMap.size()];
        int valueInd = 0;
        long totalKeysSize =0;
        long totalValuesSize = 0;
        for (var entry: codeMap.entrySet()){
            keySizes[byteInd++] = (byte)entry.getKey().getBytes().length;
            totalKeysSize += keySizes[byteInd-1];
            valueSizes[valueInd++] = (byte)entry.getValue().length();
            totalValuesSize += valueSizes[valueInd-1];
        }
        ds.write(keySizes);
        ds.write(valueSizes);
        long totalSize = totalKeysSize + totalValuesSize;
        ds.writeLong(totalSize);
        int arrSize = 1_000_000;
        byte byteArr[] = new byte[(int) Math.min(arrSize, totalSize)];
        byteInd = 0;


        for (var entry: codeMap.entrySet()){
            byte bytes[] = entry.getKey().getBytes();
            for (int iind = 0; iind < bytes.length; iind++){
                byteArr[byteInd++] = bytes[iind];
                if(byteInd==byteArr.length){
                    ds.write(byteArr);
                    byteInd = 0;
                    totalSize-=byteArr.length;
                    byteArr = new byte[(int) Math.min(arrSize, totalSize)];
                }
            }
            bytes = entry.getValue().getBytes();
            for (int iind = 0; iind < bytes.length; iind++){
                byteArr[byteInd++] = bytes[iind];
                if(byteInd==byteArr.length){
                    ds.write(byteArr);
                    byteInd = 0;
                    totalSize-=byteArr.length;
                    byteArr = new byte[(int) Math.min(arrSize, totalSize)];
                }
            }
        }

        if(byteInd != 0){
            System.out.println("Something is wrong!!!");
        }
        System.out.println("Wrote the map");
        for (long ind = 0; ind < available/SIZE;ind++) {
            data.read(buffer);
            int index = 0;
            while(index+n <= SIZE){
                byte bytes[] = new byte[n];
                for (int iind = index; iind < index+n; iind++){
                    bytes[iind - index] = buffer[iind];
                }

                String toWrite = codeMap.get(new ByteArrayWrapper(bytes));
                for (int c = 0;c < toWrite.length();c++){
                    if(toWrite.charAt(c)=='1'){
                        b |= (1<<i);
                    }
                    if(i==0){
                        bArr[bInd++] = b;
                        if(bInd == SIZE){
                            writeFile.write(bArr);
                            bInd=0;
                        }
                        b=0;
                        i=7;
                    }else{
                        i--;
                    }
                }
                index=index+n;
            }
            remaining-=SIZE;
        }

        // read left over
        if(remaining != 0){
            byte remBuffer[] = new byte[(int)remaining];
            data.read(remBuffer);

            int index = 0;
            while(index+n <= remaining){
                byte bytes[] = new byte[n];
                for (int iind = index; iind < index+n; iind++){
                    bytes[iind - index] = remBuffer[iind];
                }

                String toWrite = codeMap.get(new ByteArrayWrapper(bytes));
                for (int c = 0; c < toWrite.length();c++){
                    if(toWrite.charAt(c)=='1'){
                        b |= (1<<i);
                    }
                    if(i==0){
                        bArr[bInd++] = b;
                        if(bInd == SIZE){
                            writeFile.write(bArr);
                            bInd=0;
                        }
                        b=0;
                        i=7;
                    }else{
                        i--;
                    }
                }
                index=index+n;
            }
            if(index < remaining){
                byte bytes[] = new byte[remBuffer.length-index+1];
                for (int iind = index; iind < remBuffer.length; iind++){
                    bytes[iind - index] = remBuffer[iind];
                }

                String toWrite = codeMap.get(new ByteArrayWrapper(bytes));
                for (int c = 0; c < toWrite.length();c++){
                    if(toWrite.charAt(c)=='1'){
                        b |= (1<<i);
                    }
                    if(i==0){
                        bArr[bInd++] = b;
                        if(bInd == SIZE){
                            writeFile.write(bArr);
                            bInd=0;
                        }
                        b=0;
                        i=7;
                    }else{
                        i--;
                    }
                }
            }
        }
        if(i!=7){
            bArr[bInd++] = b;
        }
        if(bInd!=0){
            byte temp[] = new byte[bInd];
            for(int kk = 0; kk < bInd;kk++) {
                temp[kk] = bArr[kk];
            }
            writeFile.write(temp);
        }
        if(i==7){
            i = 8;
        }else{
            i = 7-i;
        }
        writeFile.write((byte)i);
        writeFile.close();
        data.close();
    }
    public static void decompress(String filename, String newFileName) throws IOException, ClassNotFoundException {
        FileInputStream data = new FileInputStream(filename);
        FileOutputStream fileWrite = new FileOutputStream(newFileName);
        System.out.println("File Size: "+data.available());

        System.out.println("read the map");
        Map<String, ByteArrayWrapper> reverseCodeMap = new HashMap<>();
        DataInputStream is = new DataInputStream(data);
        int mapSize = is.readInt();
        byte keySizes[] = new byte[mapSize];
        int keyInd = 0;
        byte valueSizes[] = new byte[mapSize];
        int valueInd = 0;
        long totalKeysSize =0;
        long totalValuesSize = 0;
        is.read(keySizes);
        is.read(valueSizes);

        long remSize = is.readLong();
        int arrSize = 100_000_000;
        byte byteBuffer[] = new byte[(int) Math.min(arrSize, remSize)];
        int byteInd = 0;
        is.read(byteBuffer);
        while(remSize > 0L){
            byte key[] = new byte[keySizes[keyInd++]];
            byte value[] = new byte[valueSizes[valueInd++]];
            for (int iind = 0; iind < key.length; iind++) {
                key[iind] = byteBuffer[byteInd++];
                if (byteInd == byteBuffer.length){
                    byteInd=0;
                    remSize -= byteBuffer.length;
                    byteBuffer = new byte[(int) Math.min(arrSize, remSize)];
                    if(remSize > 0)
                        is.read(byteBuffer);
                }
            }
            for (int iind = 0; iind < value.length; iind++) {
                value[iind] = byteBuffer[byteInd++];
                if (byteInd == byteBuffer.length){
                    byteInd=0;
                    remSize -= byteBuffer.length;
                    byteBuffer = new byte[(int) Math.min(arrSize, remSize)];
                    if(remSize > 0)
                        is.read(byteBuffer);
                }
            }
            reverseCodeMap.put(new String(value), new ByteArrayWrapper(key));
        }
        if(byteInd != 0){
            System.out.println("Something is wrong");
        }

        RandomAccessFile raf = new RandomAccessFile(filename, "r");
        // Seek to the end of file
        raf.seek(raf.length() - 1);
        // Read it out.
        byte ignTemp[] = new byte[1];
        raf.read(ignTemp);
        int ignore = ignTemp[0];
        System.out.println("ignore = " + ignore);

        final int SIZE = 100_000_000;
        byte buffer[] = new byte[SIZE];
        int available = data.available();
        long remaining = available-1;
        String s = "";
        byte outputBuffer[] = new byte[SIZE];
        int outputInd = 0;
        for (long ind = 0; ind < available/SIZE;ind++) {
            data.read(buffer);
            System.out.println("Finished "+ind);
            for(int byteIndex = 0; byteIndex < SIZE-1; byteIndex++){
                byte b = buffer[byteIndex];
                for (int bInd = 7; bInd >= 0; bInd--){
                    if((b & (1<<bInd)) != 0){
                        s += "1";
                    }else{
                        s += "0";
                    }
                    if(reverseCodeMap.containsKey(s)){
                        byte bytes[] = reverseCodeMap.get(s).getBytes();
                        for (int iind = 0; iind < bytes.length; iind++){
                            outputBuffer[outputInd++] = bytes[iind];
                            if(outputInd==SIZE){
                                fileWrite.write(outputBuffer);
                                outputInd=0;
                            }
                        }
                        s = "";
                    }
                }
            }
            byte b = buffer[buffer.length-1];
            int end = 0;
            if(remaining-SIZE == 0){
                end = 7-ignore+1;
            }
            for (int bInd = 7; bInd >= end; bInd--){
                if((b & (1<<bInd)) != 0){
                    s += "1";
                }else{
                    s += "0";
                }
                if(reverseCodeMap.containsKey(s)){
                    byte bytes[] = reverseCodeMap.get(s).getBytes();
                    for (int iind = 0; iind < bytes.length; iind++){
                        outputBuffer[outputInd++] = bytes[iind];
                        if(outputInd==SIZE){
                            fileWrite.write(outputBuffer);
                            outputInd=0;
                        }
                    }
                    s = "";
                }
            }

            remaining-=SIZE;
        }
        if(remaining!=0){
            byte remBuffer[] = new byte[(int)remaining];
            data.read(remBuffer);
            for(int kk = 0; kk < remBuffer.length-1; kk++){
                byte b = remBuffer[kk];
                for (int bInd = 7; bInd >= 0; bInd--){
                    if((b & (1<<bInd)) != 0){
                        s += "1";
                    }else{
                        s += "0";
                    }
                    if(reverseCodeMap.containsKey(s)){
                        byte bytes[] = reverseCodeMap.get(s).getBytes();
                        for (int iind = 0; iind < bytes.length; iind++){
                            outputBuffer[outputInd++] = bytes[iind];
                            if(outputInd==SIZE){
                                fileWrite.write(outputBuffer);
                                outputInd=0;
                            }
                        }
                        s = "";
                    }
                }
            }
            byte b = remBuffer[remBuffer.length-1];
            for (int ind = 7; ind >= 7-ignore+1; ind--){
                if((b & (1<<ind)) != 0){
                    s += "1";
                }else{
                    s += "0";
                }
                if(reverseCodeMap.containsKey(s)){
                    byte bytes[] = reverseCodeMap.get(s).getBytes();
                    for (int iind = 0; iind < bytes.length; iind++){
                        outputBuffer[outputInd++] = bytes[iind];
                        if(outputInd==SIZE){
                            fileWrite.write(outputBuffer);
                            outputInd=0;
                        }
                    }
                    s = "";
                }
            }
        }
        if(outputInd != 0){
            byte bytes[] = new byte[outputInd];
            for (int iind = 0; iind < outputInd; iind++) {
                bytes[iind] = outputBuffer[iind];
            }
            fileWrite.write(bytes);
        }
        raf.close();
        data.close();
        fileWrite.close();
    }
    public static void main(String[] args) {

        try{
            String c = args[0];
            String filename = args[1];
            if(c.equals("c")){
                int n = Integer.parseInt(args[2]);
                Map<ByteArrayWrapper, Integer> frequencyMap = new HashMap<>();
                long start = System.currentTimeMillis();
                readFile(filename, n, frequencyMap);
                var codeMap = encode(frequencyMap);
                String newFilename = filename.substring(filename.lastIndexOf('\\') + 1, filename.length());
                newFilename = filename.substring(0, filename.lastIndexOf('\\')) + "\\20010998." + Integer.toString(n) + "." + newFilename + ".hc";
                encodeInFile(filename,newFilename, codeMap, n);
                long end = System.currentTimeMillis();
                System.out.println(" compression time: "+ (end-start)/1000.0 + " Seconds");
                RandomAccessFile file1 = new RandomAccessFile(filename, "r");
                RandomAccessFile file2 = new RandomAccessFile(newFilename, "r");
                System.out.println("compression ratio: "+ file2.length() / (double)file1.length());
            }else{
                String newFilename = filename.substring(filename.lastIndexOf('\\') + 1, filename.lastIndexOf('.'));

                newFilename =  filename.substring(0, filename.lastIndexOf('\\')) + "\\extracted." + newFilename;
                long start = System.currentTimeMillis();
                decompress(filename, newFilename);
                long end = System.currentTimeMillis();
                System.out.println(" compression time: "+ (end-start)/1000.0 + " Seconds");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
 }


}
