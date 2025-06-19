import 'package:hackathonflutter/models/aluno.dart';

class AlunoService {
  // Simula busca de alunos via API
  Future<List<Aluno>> buscarAlunos() async {
    // Simula delay de rede
    await Future.delayed(const Duration(seconds: 1));

    // Dados simulados
    return [
      Aluno(id: 1, nome: 'Ana Silva', turma: '3º A', email: 'ana@email.com'),
      Aluno(id: 2, nome: 'Bruno Costa', turma: '3º A', email: 'bruno@email.com'),
      Aluno(id: 3, nome: 'Carlos Oliveira', turma: '3º B', email: 'carlos@email.com'),
      Aluno(id: 4, nome: 'Diana Santos', turma: '3º B', email: 'diana@email.com'),
      Aluno(id: 5, nome: 'Eduardo Lima', turma: '3º C', email: 'eduardo@email.com'),
      Aluno(id: 6, nome: 'Fernanda Rocha', turma: '3º C', email: 'fernanda@email.com'),
      Aluno(id: 7, nome: 'Gabriel Moura', turma: '3º A', email: 'gabriel@email.com'),
      Aluno(id: 8, nome: 'Helena Dias', turma: '3º B', email: 'helena@email.com'),
    ];
  }

  // Simula busca de alunos por turma via API
  Future<List<Aluno>> buscarAlunosPorTurma(int turmaId) async {
    await Future.delayed(const Duration(seconds: 1)); // Simula delay de rede

    final allStudents = await buscarAlunos();
    if (turmaId == 1) { // 3º A
      return allStudents.where((aluno) => aluno.turma == '3º A').toList();
    } else if (turmaId == 2) { // 3º B
      return allStudents.where((aluno) => aluno.turma == '3º B').toList();
    } else if (turmaId == 3) { // 3º C
      return allStudents.where((aluno) => aluno.turma == '3º C').toList();
    }
    return [];
  }
}