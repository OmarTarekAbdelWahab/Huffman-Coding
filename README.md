# Weigthed Activity Selection
The absolute path of an input file will be provided as a command-line argument to your program.  Your jar will be run using the following command:   

***java &ensp; -jar &ensp; activity_20010998.jar &ensp; absolute_path_to_input_file***  


The input file will have the number of activities n in the first line, followed by n lines. Each line will contain the start time, finish time and weight of one of the input activities. All data will be integers and will be separated by spaces, e.g., 

3  
1 2 1  
2 3 2  
3 4 5  

# Huffman-Coding
This project is an implementation of Huffman coding, a widely used compression algorithm that efficiently encodes data by assigning variable-length codes to input characters.

To use it for compressing an input file, the following will be called:  

***java &ensp; -jar &ensp; huffman_20010998.jar &ensp; c &ensp; absolute_path_to_input_file &ensp; n***  

* c means compressing the file.  
* n is the number of bytes that will be considered together.

To use it for decompressing an input file, the following be called:  

***java &ensp; -jar &ensp; huffman_20010998.jar &ensp; d &ensp; absolute_path_to_input_file***
