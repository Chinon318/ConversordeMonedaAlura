import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

//CHALLENGE CONVERSOR DE MONEDAS ALURA

abstract class CurrencyConverter {
    protected String apiEndpoint;

    public abstract double getExchangeRate(String fromCurrency, String toCurrency) throws Exception;

    public double convert(double amount, String fromCurrency, String toCurrency) throws Exception {
        double rate = getExchangeRate(fromCurrency, toCurrency);
        return amount * rate;
    }
}


class ApiCurrencyConverter extends CurrencyConverter {
    public ApiCurrencyConverter() {
        this.apiEndpoint = "https://api.exchangerate-api.com/v4/latest/";
    }

    @Override
    public double getExchangeRate(String fromCurrency, String toCurrency) throws Exception {
        URL url = new URL(apiEndpoint + fromCurrency);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        connection.disconnect();

        String jsonResponse = content.toString();
        String searchString = "\"" + toCurrency + "\":";
        int index = jsonResponse.indexOf(searchString);
        if (index == -1) {
            throw new Exception("Moneda no encontrada en la respuesta JSON");
        }
        int start = index + searchString.length();
        int end = jsonResponse.indexOf(",", start);
        if (end == -1) {
            end = jsonResponse.indexOf("}", start);
        }
        String rateString = jsonResponse.substring(start, end).trim();

        return Double.parseDouble(rateString);
    }
}


public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Ingrese la cantidad a convertir: ");
            double amount = scanner.nextDouble();

            System.out.print("Ingrese la moneda de origen (por ejemplo, USD): ");
            String fromCurrency = scanner.next().toUpperCase();

            System.out.print("Ingrese la moneda de destino (por ejemplo, EUR): ");
            String toCurrency = scanner.next().toUpperCase();

            CurrencyConverter converter = new ApiCurrencyConverter();
            double convertedAmount = converter.convert(amount, fromCurrency, toCurrency);

            System.out.println(amount + " " + fromCurrency + " es igual a " + convertedAmount + " " + toCurrency);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}