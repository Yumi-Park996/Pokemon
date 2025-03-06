import com.fasterxml.jackson.databind.ObjectMapper;
// Jackson 라이브러리의 핵심 클래스
// JSON 데이터를 자바 객체로 변환하거나, 반대로 자바 객체를 JSON으로 변환하는 기능을 제공

import com.fasterxml.jackson.annotation.JsonProperty;
// JSON 필드 이름과 자바 필드 이름이 다를 때, 두 필드를 서로 연결(매핑)해주는 애너테이션

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
// JSON 데이터에 클래스에 없는 필드가 있어도 에러를 발생시키지 않고 무시하도록 설정하는 애너테이션

import java.io.IOException;
// 입출력 관련 예외 처리에 사용하는 표준 예외 클래스 (HTTP 통신 시 발생 가능)

import java.net.URI;
// URI 객체를 생성하고 다루기 위한 클래스
// URL 문자열을 URI 객체로 변환할 때 사용

import java.net.http.HttpClient;
// HTTP 요청을 보내는 클라이언트 클래스 (Java 11 이상에서 제공)

import java.net.http.HttpRequest;
// HTTP 요청 정보를 담는 클래스 (GET/POST 여부, URL 정보 등 포함)

import java.net.http.HttpResponse;
// HTTP 응답 정보를 담는 클래스 (서버로부터 받은 데이터 저장)

import java.util.List;
// List 자료구조 (데이터 목록을 저장하는 자료구조)

import java.util.Random;
// 난수를 생성하는 클래스 (랜덤 포켓몬 번호 선택용)


public class Main {  
    // 프로그램의 진입점인 main 메서드를 가진 클래스

    public static void main(String[] args) throws IOException, InterruptedException {
        // main 메서드 선언. IOException과 InterruptedException은 HTTP 요청 처리 시 발생할 수 있는 예외들.

        String apiUrl = "https://pokeapi.co/api/v2/pokemon/%d";
        // 포켓몬 데이터를 조회할 URL 템플릿
        // %d 부분에 포켓몬 번호가 들어갈 자리 (포맷팅을 통해 완성됨)

        HttpClient client = HttpClient.newHttpClient();
        // HTTP 요청을 보내는 클라이언트 객체 생성 (Java 11 표준 기능)

        Random rand = new Random();
        // 랜덤 숫자 생성 객체 생성

        int pokemonId = rand.nextInt(1, 152);
        // 1부터 151까지의 랜덤 숫자 생성 (1세대 포켓몬만 선택)

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl.formatted(pokemonId)))
                // apiUrl의 %d에 pokemonId가 들어간 최종 URL로 URI 객체 생성
                .GET()
                // HTTP 요청 방식은 GET
                .build();
                // 최종 HttpRequest 객체 생성

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // 요청을 보내고 응답을 문자열로 받음 (JSON 문자열)

        ObjectMapper mapper = new ObjectMapper();
        // Jackson의 ObjectMapper 객체 생성
        // JSON 문자열과 자바 객체 간의 변환을 담당하는 객체

        Pokemon pokemon = mapper.readValue(response.body(), Pokemon.class);
        // 응답 받은 JSON 문자열을 Pokemon 클래스 형태로 변환
        // Pokemon.class는 Jackson이 변환할 때 참고하는 "데이터 형식 정보" (타입 정보)

        System.out.println(pokemon.sprites.frontDefault);
        // 변환된 Pokemon 객체에서 이미지 URL 출력

        String apiUrl2 = "https://pokeapi.co/api/v2/pokemon-species/%d";
        // 포켓몬 이름(언어별 이름 포함)을 조회할 URL 템플릿

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl2.formatted(pokemonId)))
                .GET()
                .build();
        // 두 번째 요청 객체 생성 (포켓몬 이름 조회용)

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        // 두 번째 요청 실행 후 응답 받기 (JSON 문자열)

        PokemonSpecies pokemonSpecies = mapper.readValue(response2.body(), PokemonSpecies.class);
        // 응답 JSON을 PokemonSpecies 객체로 변환

        System.out.println(
            pokemonSpecies.names.stream()
                    // names 리스트를 Stream으로 변환 (리스트 항목을 하나씩 처리 가능하게 만듦)
                    .filter(el -> el.language.name.equals("ko"))
                    // language.name이 "ko"인 이름만 필터링 (한국어 이름만 남김)
                    .map(el -> el.name)
                    // 남은 항목에서 이름(name)만 추출
                    .findFirst()
                    // 첫 번째 이름만 선택
                    .orElseThrow()
                    // 없으면 예외 발생 (한국어 이름이 없으면 에러)
        );
        // 최종적으로 한국어 이름 출력
    }
}

// ---------- Pokemon 클래스 ----------

@JsonIgnoreProperties(ignoreUnknown = true)
// 이 클래스에 없는 필드가 JSON에 있을 경우, 해당 필드는 무시하고 에러 발생시키지 않음
// 예를 들어, JSON에 "abilities" 같은 필드가 있어도 이 클래스엔 없으니 그냥 무시함
// 없으면: JSON 데이터에 예상치 못한 필드가 있을 때 Jackson이 에러 발생시킴

class Pokemon {
    // 포켓몬 기본 데이터를 담는 클래스
    // 필요한 필드만 정의하고, 필요 없는 필드는 @JsonIgnoreProperties로 무시하도록 설정
    // 이 클래스는 위 JSON 데이터 중 "sprites" 부분만 다룬다.
    @JsonIgnoreProperties(ignoreUnknown = true)  // ①
    // 이 애너테이션의 의미:
    // - JSON에 있는 필드 중, 이 클래스에 선언되지 않은 필드는 무시하고 넘어가라.
    // - 즉, back_default 같은 필드는 무시한다.
    // - 만약 이 애너테이션이 없고, JSON에 back_default가 있으면 매핑할 곳이 없어서 Jackson이 에러를 발생시킴.
    public static class Sprites {
        @JsonProperty("front_default")  // ②
        // 이 애너테이션의 의미:
        // - JSON의 필드 이름 "front_default"를 이 필드(frontDefault)에 매핑한다.
        // - 즉, JSON의 "front_default" 값을 자바의 frontDefault에 저장한다.
        // - JSON 필드명과 자바 필드명이 다를 때 서로 연결해주는 역할.
        public String frontDefault;  // ③
        // 실제로 데이터를 담을 자바 필드
        // - 이 필드에는 "https://example.com/pikachu.png"가 들어가게 된다.
    }
    // Pokemon 클래스 안에서 이걸 사용하는 형태
    public Sprites sprites;  // ④
    // 이 필드는 Pokemon 클래스 안에 있고, JSON의 "sprites" 전체를 매핑할 때 사용된다.
    // 즉, JSON의 "sprites" 객체가 통째로 Sprites 클래스에 매핑된다.
}

// ---------- PokemonSpecies 클래스 ----------

@JsonIgnoreProperties(ignoreUnknown = true)
// PokemonSpecies 클래스에 없는 필드가 JSON에 있을 경우, 해당 필드는 무시
// 없으면: 예상치 못한 필드 때문에 Jackson이 에러 발생시킴

class PokemonSpecies {
    // 포켓몬 종 데이터 (이름 등 다국어 정보 포함)를 담는 클래스

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Name {
        // 각 언어별 포켓몬 이름 정보를 담는 클래스

        public String name;
        // 포켓몬 이름 (예: 피카츄, Bulbasaur 등)

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Language {
            // 언어 정보를 담는 클래스 (이름이 어떤 언어인지)

            public String name;
            // 언어 이름 (ko, en 등)
        }

        public Language language;
        // 이 이름이 어떤 언어인지 저장하는 필드
    }

    public List<Name> names;
    // 각 언어별 이름 정보 목록을 저장하는 필드
}
