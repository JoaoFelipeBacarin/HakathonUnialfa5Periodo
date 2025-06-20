import 'package:shared_preferences/shared_preferences.dart';

class AuthService {
  static const String _keyEmail = 'user_email';
  static const String _keySenha = 'user_password';
  static const String _keyLogado = 'user_logged';

  // Simula autenticação com API
  Future<bool> autenticar(String email, String senha) async {
    // Simula delay de rede
    await Future.delayed(const Duration(seconds: 2));

    // Credenciais válidas simuladas
    if (email == 'teste@email.com' && senha == '12345') {
      await _salvarCredenciais(email, senha);
      return true;
    }
    return false;
  }

  // Salva credenciais localmente
  Future<void> _salvarCredenciais(String email, String senha) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_keyEmail, email);
    await prefs.setString(_keySenha, senha);
    await prefs.setBool(_keyLogado, true);
  }

  // Verifica se o usuário está logado
  Future<bool> isLogado() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getBool(_keyLogado) ?? false;
  }

  // Obtém credenciais salvas
  Future<Map<String, String>?> getCredenciais() async {
    final prefs = await SharedPreferences.getInstance();
    final email = prefs.getString(_keyEmail);
    final senha = prefs.getString(_keySenha);

    if (email != null && senha != null) {
      return {'email': email, 'senha': senha};
    }
    return null;
  }

  // Fazer logout
  Future<void> logout() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove(_keyEmail);
    await prefs.remove(_keySenha);
    await prefs.setBool(_keyLogado, false);
  }
}