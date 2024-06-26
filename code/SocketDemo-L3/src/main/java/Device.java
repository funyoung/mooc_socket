class Device {
    final int port;
    final String ip;
    final String sn;

    Device(int port, String ip, String sn) {
        this.port = port;
        this.ip = ip;
        this.sn = sn;
    }

    @Override
    public String toString() {
        return "Device{" +
                "port=" + port +
                ", ip='" + ip + '\'' +
                ", sn='" + sn + '\'' +
                '}';
    }
}
