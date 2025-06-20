// lib/services/aluno_service.dart
import 'package:hackathonflutter/models/aluno.dart';

class AlunoService {
  // Dados simulados de alunos
  final List<Aluno> _todosOsAlunos = [
    Aluno(id: 1, nome: 'Ana Silva', turma: '3º A', email: 'ana@email.com'),
    Aluno(id: 2, nome: 'Bruno Costa', turma: '3º A', email: 'bruno@email.com'),
    Aluno(id: 3, nome: 'Carlos Oliveira', turma: '3º B', email: 'carlos@email.com'),
    Aluno(id: 4, nome: 'Diana Santos', turma: '3º B', email: 'diana@email.com'),
    Aluno(id: 5, nome: 'Eduardo Lima', turma: '3º C', email: 'eduardo@email.com'),
    Aluno(id: 6, nome: 'Fernanda Rocha', turma: '3º C', email: 'fernanda@email.com'),
    Aluno(id: 7, nome: 'Gabriel Moura', turma: '3º A', email: 'gabriel@email.com'),
    Aluno(id: 8, nome: 'Helena Dias', turma: '3º B', email: 'helena@email.com'),
    // Adicione mais alunos conforme necessário para seus testes
  ];

  // Simula busca de alunos via API
  Future<List<Aluno>> buscarAlunos() async {
    // Simula delay de rede
    await Future.delayed(const Duration(seconds: 1));
    return _todosOsAlunos;
  }

  // Simula busca de alunos por turma via API
  Future<List<Aluno>> buscarAlunosPorTurma(int turmaId) async {
    await Future.delayed(const Duration(seconds: 1));
    // Mapeie turmaId para nomes de turma conforme sua lógica
    String nomeTurma;
    switch (turmaId) {
      case 1:
        nomeTurma = '3º A';
        break;
      case 2:
        nomeTurma = '3º B';
        break;
      case 3:
        nomeTurma = '3º C';
        break;
      default:
        return []; // Retorna vazio se a turma não for encontrada
    }
    return _todosOsAlunos.where((aluno) => aluno.turma == nomeTurma).toList();
  }

  // Novo método para buscar aluno por ID
  Future<Aluno?> buscarAlunoPorId(int alunoId) async {
    await Future.delayed(const Duration(milliseconds: 500)); // Simula delay de rede
    try {
      return _todosOsAlunos.firstWhere((a) => a.id == alunoId);
    } catch (e) {
      print('Aluno com ID $alunoId não encontrado: $e');
      return null;
    }
  }
}