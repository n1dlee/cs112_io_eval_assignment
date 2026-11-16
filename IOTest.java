package cs112_io_eval_assignment;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

public class IOTest {
    public static void main(String[] args) throws IOException {
        ArrayList<Double> doubles = new ArrayList<>();
        doubles.add(2.5);
        doubles.add(-7.0);
        doubles.add(0.0);

        ArrayList<Double> emptyList = new ArrayList<>();

        DoubleFileConverter.writeTextFile("doubles.txt", doubles);

        ArrayList<Double> readDoubles =
                DoubleFileConverter.readTextFile("doubles.txt");

        System.out.println("Read doubles from file: " + readDoubles);
        if (!doubles.equals(readDoubles)) {
            throw new AssertionError("Text file values do not match");
        }
        System.out.println("PASS: text file");

        DoubleFileConverter.writeTextFile("empty.txt", emptyList);

        ArrayList<Double> readEmptyList =
                DoubleFileConverter.readTextFile("empty.txt");

        if (!emptyList.equals(readEmptyList)) {
            throw new AssertionError("Expected an empty text list");
        }
        System.out.println("PASS: empty text file");

        try (FileWriter writer = new FileWriter("invalid.txt")) {
            writer.write("2.5\nhello\n");
        }

        try {
            DoubleFileConverter.readTextFile("invalid.txt");
            throw new AssertionError("Invalid text value was accepted");
        } catch (IllegalArgumentException e) {
            System.out.println("PASS: invalid text value rejected");
        }

        DoubleFileConverter.writeBinaryFile("numbers.bin", doubles);

        ArrayList<Double> readBinaryDoubles =
                DoubleFileConverter.readBinaryFile("numbers.bin");

        System.out.println("Read binary doubles: " + readBinaryDoubles);
        if (!doubles.equals(readBinaryDoubles)) {
            throw new AssertionError("Binary file values do not match");
        }
        System.out.println("PASS: binary file");

        DoubleFileConverter.writeBinaryFile("empty.bin", emptyList);

        ArrayList<Double> readBinaryEmptyList =
                DoubleFileConverter.readBinaryFile("empty.bin");

        if (!emptyList.equals(readBinaryEmptyList)) {
            throw new AssertionError("Expected an empty binary list");
        }
        System.out.println("PASS: empty binary file");

        long fileSize = new File("numbers.bin").length();

        if (fileSize != 24) {
            throw new AssertionError("Expected 24 bytes, got " + fileSize);
        }
        System.out.println("PASS: binary file size is 24 bytes");

        DoubleFileConverter.writeBinaryListFile("list.bin", doubles);

        ArrayList<Double> readBinaryList =
                DoubleFileConverter.readBinaryListFile("list.bin");

        System.out.println("Read binary list: " + readBinaryList);
        if (!doubles.equals(readBinaryList)) {
            throw new AssertionError("Serialized list values do not match");
        }
        System.out.println("PASS: serialized list");

        DoubleFileConverter.writeBinaryListFile("empty-list.bin", emptyList);

        ArrayList<Double> readEmptyBinaryList =
                DoubleFileConverter.readBinaryListFile("empty-list.bin");

        if (!emptyList.equals(readEmptyBinaryList)) {
            throw new AssertionError("Expected an empty serialized list");
        }
        System.out.println("PASS: empty serialized list");

        File twoBitFile = new File("two-bit-test.bin");
        int[] expected = {0, 1, 2, 3, 3, 2, 1, 0};

        try (TwoBitRandomAccessFile data =
                new TwoBitRandomAccessFile(twoBitFile.getPath(), 8)) {

            if (twoBitFile.length() != 2) {
                throw new AssertionError("Expected 2 bytes");
            }

            for (int i = 0; i < expected.length; i++) {
                data.set(i, expected[i]);
            }

            for (int i = 0; i < expected.length; i++) {
                if (data.get(i) != expected[i]) {
                    throw new AssertionError("Incorrect value at position " + i);
                }
            }
            System.out.println("PASS: two-bit writing and reading");

            try (RandomAccessFile raw = new RandomAccessFile(twoBitFile, "r")) {
                int firstByte = raw.readUnsignedByte();
                int secondByte = raw.readUnsignedByte();

                if (firstByte != 27 || secondByte != 228) {
                    throw new AssertionError("Incorrect two-bit byte format");
                }
            }
            System.out.println("PASS: two-bit byte format");

            data.set(1, 3);
            expected[1] = 3;

            for (int i = 0; i < expected.length; i++) {
                if (data.get(i) != expected[i]) {
                    throw new AssertionError(
                            "Incorrect value after update at position " + i);
                }
            }
            System.out.println("PASS: updating preserves neighbors");

            long[] invalidPositions = {-1, 8, Long.MAX_VALUE};

            for (long position : invalidPositions) {
                try {
                    data.get(position);
                    throw new AssertionError(
                            "get accepted position " + position);
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("PASS: get rejected " + position);
                }

                try {
                    data.set(position, 1);
                    throw new AssertionError(
                            "set accepted position " + position);
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("PASS: set rejected " + position);
                }
            }

            int[] invalidValues = {-1, 4};

            for (int value : invalidValues) {
                try {
                    data.set(0, value);
                    throw new AssertionError("set accepted value " + value);
                } catch (IllegalArgumentException e) {
                    System.out.println("PASS: rejected value " + value);
                }
            }
        }

        try (TwoBitRandomAccessFile data =
                new TwoBitRandomAccessFile(twoBitFile)) {

            for (int i = 0; i < expected.length; i++) {
                if (data.get(i) != expected[i]) {
                    throw new AssertionError(
                            "Value lost after reopening at position " + i);
                }
            }
            System.out.println("PASS: values survive reopening");

            data.clear();

            for (int i = 0; i < expected.length; i++) {
                if (data.get(i) != 0) {
                    throw new AssertionError(
                            "clear failed at position " + i);
                }
            }

            if (twoBitFile.length() != 2) {
                throw new AssertionError("clear changed the file size");
            }
            System.out.println("PASS: clear resets values and preserves size");
        }

        try (TwoBitRandomAccessFile data =
                new TwoBitRandomAccessFile(twoBitFile.getPath())) {

            for (int i = 0; i < expected.length; i++) {
                if (data.get(i) != 0) {
                    throw new AssertionError(
                            "Cleared values were not preserved");
                }
            }
            System.out.println("PASS: cleared values survive reopening");
        }

        File roundedFile = new File("two-bit-five.bin");

        try (TwoBitRandomAccessFile data = new TwoBitRandomAccessFile(roundedFile, 5)) {

            if (roundedFile.length() != 2) {
                throw new AssertionError("5 values should require 2 bytes");
            }
            System.out.println("PASS: size rounds up");
        }

        File zeroFile = new File("two-bit-zero.bin");

        try (TwoBitRandomAccessFile data = new TwoBitRandomAccessFile(zeroFile.getPath(), 0)) {

            data.clear();

            if (zeroFile.length() != 0) {
                throw new AssertionError("Expected an empty file");
            }

            try {
                data.get(0);
                throw new AssertionError("Empty file accepted reading");
            } catch (IndexOutOfBoundsException e) {
                System.out.println("PASS: empty file rejects reading");
            }

            try {
                data.set(0, 1);
                throw new AssertionError("Empty file accepted writing");
            } catch (IndexOutOfBoundsException e) {
                System.out.println("PASS: empty file rejects writing");
            }
        }

        System.out.println("All tests passed.");
    }
}