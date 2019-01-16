public class MyURL {
  private final String protocol;
  private final String host;
  private final int port;
  private final String path;

  public MyURL(String url) {
    // split url in no more than 4 tokens
    // http: / / host[:port] / path
    // 0 ... 1 . 2 ........... 3
    if (url == null)
      throw new IllegalArgumentException("Invalid url: " + url);
    String[] tokens = url.split("/", 4);
    // fetching protocol
    if (!tokens[0].endsWith(":") || tokens.length < 3
        || tokens[1].length() > 0) {
      throw new IllegalArgumentException(
          "Url: " + url + " should start with <protocol>://");
    }
    protocol = tokens[0].substring(0, tokens[0].length() - 1);
    // fetching host + port
    if (tokens[2].length() == 0) {
      throw new IllegalArgumentException(
          "Url: " + url + " should contain host[:port] specification");
    }
    // split host:port
    String[] address = tokens[2].split(":", -1);
    host = address[0];
    if (address.length > 2)
      throw new IllegalArgumentException(
          "Invalid host[:port] specification :" + tokens[2]);
    if (address.length == 2)
      try {
        port = Integer.parseInt(address[1]);
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException(
            "Invalid port number: " + address[1]);
      }
    else
      port = -1;
    // fetching path
    if (tokens.length < 4) {
      throw new IllegalArgumentException(
          "Url: " + url + " an empty path is not allowed");
    }
    path = '/' + tokens[3];
  }

  public String getProtocol() {
    return protocol;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public String getPath() {
    return path;
  }

  @Override
  public String toString() {
    return protocol + "://" + host + (port >= 0 ? ":" + port : "") + path;
  }
}
