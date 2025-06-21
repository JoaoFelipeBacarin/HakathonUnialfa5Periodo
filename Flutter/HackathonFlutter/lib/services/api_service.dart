import 'dart:convert';
import 'package:http/http.dart' as http;

class ApiService {
  // Use 'http://10.0.2.2:8080' para emuladores Android (endereço do host loopback)
  // Use 'http://localhost:8080' para iOS Simulator ou Chrome/Web
  // Para dispositivos físicos, você precisará do IP da sua máquina na rede local
  // Ex: final String _baseUrl = 'http://192.168.1.100:8080';
  final String _baseUrl = 'http://192.168.0.104:8080'; // Endereço para emulador Android

  // Método para fazer requisições POST genéricas (futuramente para outras APIs)
  Future<Map<String, dynamic>> post(String path, Map<String, dynamic> data) async {
    final uri = Uri.parse('$_baseUrl$path');
    try {
      final response = await http.post(
        uri,
        headers: <String, String>{
          'Content-Type': 'application/json; charset=UTF-8',
        },
        body: jsonEncode(data),
      );

      if (response.statusCode >= 200 && response.statusCode < 300) {
        // Sucesso (status 2xx)
        return jsonDecode(response.body);
      } else {
        // Erro no servidor ou na requisição (status diferente de 2xx)
        final errorBody = jsonDecode(response.body);
        throw Exception('Erro na requisição POST para $path: ${response.statusCode} - ${errorBody['message'] ?? 'Erro desconhecido'}');
      }
    } catch (e) {
      // Erro de rede ou outro erro
      throw Exception('Falha ao conectar ao servidor para $path: $e');
    }
  }

  // Método específico para login
  Future<Map<String, dynamic>> login(String email, String senha) async {
    return await post('/auth/login', {
      'email': email,
      'senha': senha, // Atenção: Em um sistema real, senhas nunca deveriam ser enviadas em texto puro em logs/prints!
    });
  }

  // O método sendGabaritoData pode ser refatorado para usar o método post
  Future<Map<String, dynamic>> sendGabaritoData(Map<String, dynamic> data) async {
    // Exemplo de como usar o método `post` genérico
    return await post('/avaliacoes', data); // Supondo que a API de avaliações seja '/avaliacoes'
  }
}