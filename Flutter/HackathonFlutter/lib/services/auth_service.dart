import 'dart:convert'; // Para jsonDecode e jsonEncode
import 'package:http/http.dart' as http; // Para requisições HTTP
import 'package:shared_preferences/shared_preferences.dart';

class AuthService {
  // Base URL da sua API. Para emuladores Android, '10.0.2.2' mapeia para localhost do PC.
  // Para iOS Simulator ou device real, use o IP real da sua máquina ou um domínio.
  static const String _baseUrl = 'http://192.168.0.104:8080/api';

  // Chaves para SharedPreferences
  static const String _keyToken = 'auth_token';
  static const String _keyLogado = 'user_logged';
  static const String _keyUserId = 'user_id';
  static const String _keyUsername = 'user_username';
  static const String _keyUserNome = 'user_nome';
  static const String _keyUserEmail = 'user_email';
  static const String _keyUserTipo = 'user_tipo';

  Future<bool> autenticar(String username, String password) async {
    final url = Uri.parse('$_baseUrl/auth/login');
    try {
      final response = await http.post(
        url,
        headers: <String, String>{
          'Content-Type': 'application/json; charset=UTF-8',
        },
        body: jsonEncode(<String, String>{
          'username': username,
          'password': password,
        }),
      );

      if (response.statusCode == 200) {
        final Map<String, dynamic> responseData = jsonDecode(response.body);
        await _salvarDadosUsuario(responseData);
        return true;
      } else {
        // Tratar erros de autenticação (ex: 401 Unauthorized)
        print('Falha na autenticação: ${response.statusCode} - ${response.body}');
        return false;
      }
    } catch (e) {
      print('Erro ao tentar autenticar: $e');
      return false;
    }
  }

  Future<void> _salvarDadosUsuario(Map<String, dynamic> data) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_keyToken, data['token']);
    await prefs.setBool(_keyLogado, true);
    await prefs.setInt(_keyUserId, data['userId']);
    await prefs.setString(_keyUsername, data['username']);
    await prefs.setString(_keyUserNome, data['nome']);
    await prefs.setString(_keyUserEmail, data['email']);
    await prefs.setString(_keyUserTipo, data['tipo']);
  }

  Future<bool> isLogado() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getBool(_keyLogado) ?? false;
  }

  Future<String?> getToken() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString(_keyToken);
  }

  Future<Map<String, dynamic>?> getDadosUsuario() async {
    final prefs = await SharedPreferences.getInstance();
    if (prefs.getBool(_keyLogado) ?? false) {
      return {
        'userId': prefs.getInt(_keyUserId),
        'username': prefs.getString(_keyUsername),
        'nome': prefs.getString(_keyUserNome),
        'email': prefs.getString(_keyUserEmail),
        'tipo': prefs.getString(_keyUserTipo),
      };
    }
    return null;
  }

  Future<void> logout() async {
    final prefs = await SharedPreferences.getInstance();
    // Limpa todos os dados de autenticação
    await prefs.remove(_keyToken);
    await prefs.remove(_keyLogado);
    await prefs.remove(_keyUserId);
    await prefs.remove(_keyUsername);
    await prefs.remove(_keyUserNome);
    await prefs.remove(_keyUserEmail);
    await prefs.remove(_keyUserTipo);
  }

  // Novo método para validar o token com a API
  Future<bool> validarToken() async {
    final token = await getToken();
    if (token == null) {
      return false;
    }

    final url = Uri.parse('$_baseUrl/auth/validate');
    try {
      final response = await http.post(
        url,
        headers: <String, String>{
          'Content-Type': 'application/json; charset=UTF-8',
          'Authorization': 'Bearer $token',
        },
      );

      if (response.statusCode == 200) {
        final Map<String, dynamic> responseData = jsonDecode(response.body);
        return responseData['valid'] ?? false;
      } else {
        print('Validação de token falhou: ${response.statusCode} - ${response.body}');
        return false;
      }
    } catch (e) {
      print('Erro ao validar token: $e');
      return false;
    }
  }
}