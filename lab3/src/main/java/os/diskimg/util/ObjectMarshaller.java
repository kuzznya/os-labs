package os.diskimg.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ObjectMarshaller {

    private final Endianness endianness;

    public ObjectMarshaller(Endianness endianness) {
        this.endianness = endianness;
    }

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

        Field[] fields = type.getDeclaredFields();

        int alignment = 0;
        if (obj instanceof Alignable)
            alignment = ((Alignable) obj).alignment();

        ByteArrayOutputStream data = new ByteArrayOutputStream();

        for (Field field : fields) {
            try {
                field.setAccessible(true);

                if (field.getAnnotation(IgnoreField.class) != null)
                    continue;

                Object fieldValue = field.get(obj);
                if (fieldValue == null)
                    continue;

                data.write(marshall(fieldValue));
            } catch (Exception ignored) {}
        }
        while (data.size() < alignment)
            data.write(0);
        return data.toByteArray();
    }

    private byte[] marshallArray(Object obj) {
        if (!obj.getClass().isArray())
            throw new IllegalArgumentException("Object is not an array");

        ByteArrayOutputStream data = new ByteArrayOutputStream();
        try {
            int i = 0;
            while (true)
                data.write(marshall(Array.get(obj, i++)));

        } catch (Exception ex) {
            return data.toByteArray();
        }
    }

    private byte[] marshallByte(byte value) {
        return new byte[] {value};
    }

    private byte[] marshallShort(short value) {
        byte[] data = new byte[] {
                (byte) (value >> 8),
                (byte) (value)};
        if (endianness == Endianness.LITTLE_ENDIAN)
            return reverse(data);
        else
            return data;
    }

    private byte[] marshallInt(int value) {
        byte[] data = new byte[] {
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) (value)};
        if (endianness == Endianness.LITTLE_ENDIAN)
            return reverse(data);
        else
            return data;
    }

    private byte[] marshallLong(long value) {
        byte[] data = new byte[] {
                (byte) (value >> 56),
                (byte) (value >> 48),
                (byte) (value >> 40),
                (byte) (value >> 32),
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) (value)};
        if (endianness == Endianness.LITTLE_ENDIAN)
            return reverse(data);
        else
            return data;
    }

    private byte[] marshallChar(char value) {
        try {
            byte[] data = StandardCharsets.US_ASCII
                    .newEncoder()
                    .encode(CharBuffer.wrap(new char[]{value}))
                    .array();
            if (endianness == Endianness.LITTLE_ENDIAN)
                return reverse(data);
            else
                return data;
        } catch (CharacterCodingException ex) {
            throw new RuntimeException("Cannot encode char to UTF-8", ex);
        }
    }

    private byte[] marshallString(String value) {
        try {
            return StandardCharsets.US_ASCII
                    .newEncoder()
                    .encode(CharBuffer.wrap(value))
                    .array();
        } catch (CharacterCodingException ex) {
            throw new RuntimeException("Cannot encode char to UTF-8", ex);
        }
    }

    private byte[] marshallList(List<Object> values) {
        try {
            ByteArrayOutputStream data = new ByteArrayOutputStream();
            for (Object value : values)
                data.write(marshall(value));
            return data.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private byte[] reverse(byte[] data) {
        byte[] result = new byte[data.length];
        for (int i = 0; i < result.length; i++)
            result[i] = data[data.length - i - 1];
        return result;
    }
}
