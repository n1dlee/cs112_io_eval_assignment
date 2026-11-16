import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.IOException;

public class TwoBitRandomAccessFile implements AutoCloseable {
    
    private RandomAccessFile file;

    public TwoBitRandomAccessFile(File file) throws FileNotFoundException {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }
        this.file = new RandomAccessFile(file, "rw");
        
    }

    public TwoBitRandomAccessFile(String filename) throws FileNotFoundException {
        if(filename == null || filename.isEmpty()){
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }
        this.file = new RandomAccessFile(filename, "rw");
    }

    public TwoBitRandomAccessFile(File file, int numTwoBits) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }
        if (numTwoBits < 0) {
            throw new IllegalArgumentException("Number of two bits cannot be negative");
        }
        this.file = new RandomAccessFile(file, "rw");
        long numBytes = (numTwoBits + 3L) / 4; 
        this.file.setLength(numBytes);
        
    }

    public TwoBitRandomAccessFile(String filename, int numTwoBits) throws IOException {
        if(filename == null || filename.isEmpty()){
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }
        if (numTwoBits < 0) {
            throw new IllegalArgumentException("Number of two bits cannot be negative");
        }
        this.file = new RandomAccessFile(filename, "rw");
        long numBytes = (numTwoBits + 3L) / 4; 
        this.file.setLength(numBytes);
    }

    public void clear() throws IOException {
        long currentLength = file.length();
        file.seek(0);
        for(long i = 0; i < currentLength; i++){
            file.writeByte(0);
        }
    }

    public int get(long pos) throws IOException {
        if(pos < 0){
            throw new IndexOutOfBoundsException("Position cannot be negative");
        }

        long byteIndex = pos / 4;
        if(byteIndex >= file.length()){
            throw new IndexOutOfBoundsException("Position exceeds file length");
        }
        file.seek(byteIndex);
        int byteValue = file.readUnsignedByte();
        int bitPosition = (int) (6 - 2 * (pos % 4));
        return (byteValue >> bitPosition) & 3;
    }

    public void set(long pos, int val) throws IOException {
        if(val < 0 || val > 3){
            throw new IllegalArgumentException("Value must be between 0 and 3");
        }

        if(pos < 0){
            throw new IndexOutOfBoundsException("Position cannot be negative");
        }

        long byteIndex = pos / 4;
        if(byteIndex >= file.length()){
            throw new IndexOutOfBoundsException("Position exceeds file length");
        }
        file.seek(byteIndex);
        int byteValue = file.readUnsignedByte();
        int bitPosition = (int) (6 - 2 * (pos % 4));
        int mask = 3 << bitPosition;
        int clearedByte = byteValue & ~mask;
        int shiftedValue = val << bitPosition;
        int updatedByte = clearedByte | shiftedValue;

        file.seek(byteIndex);
        file.writeByte(updatedByte);
    }

    @Override
    public void close() throws IOException {
        if (file != null) {
            file.close();
        }
    }
}
