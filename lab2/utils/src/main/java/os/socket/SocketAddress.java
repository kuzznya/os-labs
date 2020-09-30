package os.socket;

public class SocketAddress {
    private final Domain domain;
    private final char[] data;

    public SocketAddress(Domain domain, char[] data) {
        this.domain = domain;
        this.data = data;
    }

    public Domain getDomain() {
        return domain;
    }

    public char[] getData() {
        return data;
    }

    public char[] toCharArray() {
        char[] data = new char[16];
        short domainValue = domain.getNativeValue();
        data[0] = (char) (domainValue & 0xFF);
        data[1] = (char) ((domainValue >> 8) & 0xFF);
        for (int i = 2; i < this.data.length && i < 16; i++)
            data[i] = this.data[i];
        return data;
    }

    public int getSize() {
        return 2 + data.length;
    }
}
