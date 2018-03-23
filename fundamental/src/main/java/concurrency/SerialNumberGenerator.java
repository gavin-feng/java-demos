package concurrency;

public class SerialNumberGenerator {
  private static volatile int serialNumber = 0;
  public static int nextSerialNumber() {
    return serialNumber++; // Not thread-safe； 方法上加 synchronized 可以解决
  }
} ///:~
