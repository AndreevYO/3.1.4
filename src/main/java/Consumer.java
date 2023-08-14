import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class Consumer {
    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();

        String url = "http://94.198.50.185:7081/api/users";
        String urlForDelete = "http://94.198.50.185:7081/api/users/3"; // Удаление пользователя с ID 3

        // Получаем весь список пользователей и сохраняем значение Session ID из куки
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        HttpHeaders httpHeaders = responseEntity.getHeaders();

        System.out.println(httpHeaders);

        // Добавляем Session ID к заголовкам запроса
        List<String> cookies = httpHeaders.get("Set-Cookie");

        String sessionID = extractSessionID(cookies);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set("Cookie", "JSESSIONID=" + sessionID);

        // Добавляем нового пользователя
        User user = new User(3L, "John", "Brown", (byte) 67);
        HttpEntity<User> requestForPost = new HttpEntity<>(user, requestHeaders);

        ResponseEntity<String> saveResponseEntity = restTemplate.exchange(url, HttpMethod.POST, requestForPost, String.class);
        System.out.println(saveResponseEntity.getBody());

        //изменяем пользователя
        User updatedUser = new User(3L, "Thomas", "Shelby", (byte) 30);

        HttpEntity<User> requestForUpdate = new HttpEntity<>(updatedUser, requestHeaders);

        ResponseEntity<String> updateResponseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestForUpdate, String.class);
        System.out.println(updateResponseEntity.getBody());

        // Удаляем пользователя

        HttpEntity<User> requestForDelete = new HttpEntity<>(requestHeaders);

        ResponseEntity<String> deleteResponseEntity = restTemplate.exchange(urlForDelete, HttpMethod.DELETE, requestForDelete, String.class);
        System.out.println(deleteResponseEntity.getBody());

    }

    private static String extractSessionID(List<String> cookies) {
        for (String cookie : cookies) {
            if (cookie.startsWith("JSESSIONID=")) {
                return cookie.substring("JSESSIONID=".length(), cookie.indexOf(';'));
            }
        }
        return null;
    }

}
