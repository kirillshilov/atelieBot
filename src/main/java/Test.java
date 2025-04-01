import java.nio.charset.StandardCharsets;

public class Test {
    public static void main(String[] args) {
        String phoneNumber = "+79267023573";
        byte[] bytes = phoneNumber.getBytes(StandardCharsets.UTF_8);
        String byteString = new String(bytes, StandardCharsets.UTF_8);
        System.out.println(byteString);  // Вернет строку, из которой байты были получены
    }
}
