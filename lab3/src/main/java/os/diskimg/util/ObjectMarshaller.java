package os.diskimg.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ObjectMarshaller {

    @SuppressWarnings("unchecked")
    public byte[] marshall(Object obj) {
        Class<?> type = obj.getClass();

        if (obj instanceof Marshallable)
            return ((Marshallable) obj).marshall();

        if (type.isArray())
            return marshallArray(obj);

        if (obj instanceof Byte)
            return marshallByte((Byte) obj);
        if (obj instanceof Short)
            return marshallShort((Short) obj);
        if (obj instanceof Integer)
            return marshallInt((Integer) obj);
        if (obj instanceof Long)
            return marshallLong((Long) obj);
        if (obj instanceof Character)
            return marshallChar((Character) obj);
        if (obj instanceof String)
            return marshallString((String) obj);

        if (obj instanceof List)
            return marshallList((List<Object>) obj);

        Field[] fields = type.getFields();

        ArrayList<Byte> data = new ArrayList<>();

        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object fieldValue = field.get(obj);
                if (fieldValue == null)
                    continue;
                byte[] elemBytes = marshall(fieldValue);
                for (byte val : elemBytes)
                    data.add(val);
            } catch (Exception ignored) {}
        }

        byte[] result = new byte[data.size()];
        for (int i = 0; i < result.length; i++)
            result[i] = data.get(i);
        return result;
    }

    private byte[] marshallArray(Object obj) {
        if (!obj.getClass().isArray())
            throw new IllegalArgumentException("Object is not an array");

        ArrayList<Byte> data = new ArrayList<>();
        try {
            int i = 0;
            while (true) {
                byte[] elemBytes = marshall(Array.get(obj, i++));
                for (byte val : elemBytes)
                    data.add(val);
            }
        } catch (Exception ex) {
            byte[] result = new byte[data.size()];
            for (int i = 0; i < result.length; i++)
                result[i] = data.get(i);
            return result;
        }
    }

    private byte[] marshallByte(byte value) {
        return new byte[] {value};
    }

    private byte[] marshallShort(short value) {
        return new byte[] {
                (byte) (value >> 8),
                (byte) (value)};
    }

    private byte[] marshallInt(int value) {
        return new byte[] {
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) (value)};
    }

    private byte[] marshallLong(long value) {
        return new byte[] {
                (byte) (value >> 56),
                (byte) (value >> 48),
                (byte) (value >> 40),
                (byte) (value >> 32),
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) (value)};
    }

    private byte[] marshallChar(char value) {
//        if (StandardCharsets.US_ASCII.newEncoder().canEncode(value))
//            return new byte[] {(byte) value};
        try {
            return StandardCharsets.UTF_8
                    .newEncoder()
                    .encode(CharBuffer.wrap(new char[]{value}))
                    .array();
        } catch (CharacterCodingException ex) {
            throw new RuntimeException("Cannot encode char to UTF-8", ex);
        }
    }

    private byte[] marshallString(String value) {
        try {
            return StandardCharsets.UTF_8
                    .newEncoder()
                    .encode(CharBuffer.wrap(value))
                    .array();
        } catch (CharacterCodingException ex) {
            throw new RuntimeException("Cannot encode char to UTF-8", ex);
        }
    }

    private byte[] marshallList(List<Object> values) {
        ArrayList<Byte> data = new ArrayList<>();
        for (Object value : values) {
            byte[] elemBytes = marshall(value);
            for (byte b : elemBytes)
                data.add(b);
        }

        byte[] result = new byte[data.size()];
        for (int i = 0; i < result.length; i++)
            result[i] = data.get(i);
        return result;
    }
}
