import 'package:shared_preferences/shared_preferences.dart';
import 'package:hackathonflutter/services/api_service.dart'; // Importe o ApiService

class AuthService {
  static const String _keyUserToken = 'user_token'; // Chave para armazenar o token
  static const String _keyLoggedIn = 'is_logged_in'; // Chave para status de login

  final ApiService _apiService = ApiService(); // Instância do ApiService

  // Remove a simulação e faz a chamada real para a API de login
  Future<bool> autenticar(String email, String senha) async {
    try {
      final response = await _apiService.login(email, senha);

      // Supondo que a API retorna um token na resposta (ex: {'token': '...', 'user': {...}})
      if (response['token'] != null && response['token'].isNotEmpty) {
        await _salvarToken(response['token']);
        return true;
      } else {
        // Se a API não retornar um token mas indicar sucesso de outra forma, ajuste aqui
        // Por exemplo, se retornar apenas um status 'success': true
        return response['status'] == 'success';
      }
    } catch (e) {
      print('Erro na autenticação: $e');
      return false; // Falha na autenticação
    }
  }

  // Salva o token localmente (ou qualquer outro dado de sessão)
  Future<void> _salvarToken(String token) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_keyUserToken, token);
    await prefs.setBool(_keyLoggedIn, true);
  }

  // Verifica se o usuário está logado (verificando a existência do token)
  Future<bool> isLogado() async {
    final prefs = await SharedPreferences.getInstance();
    final token = prefs.getString(_keyUserToken);
    return token != null && token.isNotEmpty && (prefs.getBool(_keyLoggedIn) ?? false);
  }

  // Obtém o token salvo (útil para adicionar em headers de requisições autenticadas)
  Future<String?> getToken() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString(_keyUserToken);
  }

  // Realiza o logout, limpando as credenciais locais
  Future<void> logout() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove(_keyUserToken);
    await prefs.setBool(_keyLoggedIn, false); // Define explicitly as false
  }

// Removendo getCredenciais, pois não armazenaremos mais email/senha em texto puro
// e sim o token para autenticação posterior.
// Se precisar de dados do usuário logado (nome, email), a API deve ter um endpoint '/me'
// que retorna esses dados usando o token.
}