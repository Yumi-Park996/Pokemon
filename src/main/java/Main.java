import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // JsonIgnoreProperties 추가

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        String apiUrl = "https://pokeapi.co/api/v2/pokemon/%d";
        HttpClient client = HttpClient.newHttpClient();
        Random rand = new Random();
        int pokemonId = rand.nextInt(1, 152);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl.formatted(pokemonId))).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        Pokemon pokemon = mapper.readValue(response.body(), Pokemon.class);
        if (pokemon.sprites != null && pokemon.sprites.frontDefault != null) {
            System.out.println(pokemon.sprites.frontDefault);
        } else {
            System.out.println("포켓몬 이미지를 찾을 수 없습니다. (ID: " + pokemonId + ")");
        }
    }
}

@JsonIgnoreProperties(ignoreUnknown = true) // 추가
class Pokemon {
    @JsonIgnoreProperties(ignoreUnknown = true) // 추가
    public static class Sprites {
        @JsonProperty("front_default")
        public String frontDefault;
    }

    public Sprites sprites;
}