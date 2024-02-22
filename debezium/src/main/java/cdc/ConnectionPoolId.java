package cdc;


import java.io.Serializable;
import java.util.Objects;

/** The connection pool identifier. */
public class ConnectionPoolId implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String host;
    private final int port;

    public ConnectionPoolId(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConnectionPoolId)) {
            return false;
        }
        ConnectionPoolId that = (ConnectionPoolId) o;
        return Objects.equals(host, that.host) && Objects.equals(port, that.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }

    @Override
    public String toString() {
        return host + ':' + port;
    }
}