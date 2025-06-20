import 'package:hackathonflutter/models/questao.dart';
import 'package:hackathonflutter/models/resposta.dart';
import 'package:hackathonflutter/models/turma.dart';
import 'package:hackathonflutter/models/prova.dart';

class AvaliacaoService {
  Future<List<Turma>> buscarTurmas() async {
    await Future.delayed(const Duration(seconds: 1));
    return [
      Turma(id: 1, nome: '3º A', ano: '2025'),
      Turma(id: 2, nome: '3º B', ano: '2025'),
      Turma(id: 3, nome: '3º C', ano: '2025'),
    ];
  }

  Future<List<Prova>> buscarProvasPorTurma(int turmaId) async {
    await Future.delayed(const Duration(seconds: 1));
    if (turmaId == 1) {
      return [
        Prova(id: 101, nome: 'Prova de Português', disciplina: 'Português', turmaId: 1),
        Prova(id: 102, nome: 'Prova de Matemática', disciplina: 'Matemática', turmaId: 1),
      ];
    } else if (turmaId == 2) {
      return [
        Prova(id: 201, nome: 'Prova de História', disciplina: 'História', turmaId: 2),
        Prova(id: 202, nome: 'Prova de Geografia', disciplina: 'Geografia', turmaId: 2),
      ];
    }
    return [];
  }

  Future<Prova?> buscarProvaPorId(int provaId) async {
    await Future.delayed(const Duration(seconds: 0));
    List<Prova> todasProvas = [
      Prova(id: 101, nome: 'Prova de Português', disciplina: 'Português', turmaId: 1),
      Prova(id: 102, nome: 'Prova de Matemática', disciplina: 'Matemática', turmaId: 1),
      Prova(id: 201, nome: 'Prova de História', disciplina: 'História', turmaId: 2),
      Prova(id: 202, nome: 'Prova de Geografia', disciplina: 'Geografia', turmaId: 2),
      Prova(id: 2, nome: 'Simulado Geral', disciplina: 'Variadas', turmaId: 0),
    ];

    // **MUDANÇA AQUI: Usando um loop for para encontrar a prova**
    for (var prova in todasProvas) {
      if (prova.id == provaId) {
        return prova;
      }
    }
    return null; // Retorna null se a prova não for encontrada após o loop
  }

  Future<List<Resposta>> buscarRespostasAluno(int alunoId, int provaId) async {
    await Future.delayed(const Duration(seconds: 1));
    if (alunoId == 1 && provaId == 101) {
      return [
        Resposta(alunoId: 1, questaoNumero: 1, alternativaSelecionada: 'A', dataResposta: DateTime.now()),
        Resposta(alunoId: 1, questaoNumero: 2, alternativaSelecionada: 'C', dataResposta: DateTime.now()),
      ];
    } else if (alunoId == 2 && provaId == 101) {
      return [
        Resposta(alunoId: 2, questaoNumero: 1, alternativaSelecionada: 'B', dataResposta: DateTime.now()),
        Resposta(alunoId: 2, questaoNumero: 3, alternativaSelecionada: 'C', dataResposta: DateTime.now()),
        Resposta(alunoId: 2, questaoNumero: 5, alternativaSelecionada: 'E', dataResposta: DateTime.now()),
      ];
    }
    return [];
  }

  Future<void> enviarRespostasAluno(
      int alunoId, int provaId, List<Resposta> respostas) async {
    await Future.delayed(const Duration(seconds: 2));
    print('Enviando respostas para o Aluno ID: $alunoId, Prova ID: $provaId');
    for (var resposta in respostas) {
      print('  Questão: ${resposta.questaoNumero}, Alternativa: ${resposta.alternativaSelecionada}');
    }
  }

  Future<List<Questao>> buscarGabaritoOficial(int provaId) async {
    await Future.delayed(const Duration(seconds: 1));
    if (provaId == 101) {
      return [
        Questao(numero: 1, alternativas: {'A': 'Alternativa A', 'B': 'Alternativa B', 'C': 'Alternativa C', 'D': 'Alternativa D', 'E': 'Alternativa E'}),
        Questao(numero: 2, alternativas: {'A': 'Alternativa A', 'B': 'Alternativa B', 'C': 'Alternativa C', 'D': 'Alternativa D', 'E': 'Alternativa E'}),
        Questao(numero: 3, alternativas: {'A': 'Alternativa A', 'B': 'Alternativa B', 'C': 'Alternativa C', 'D': 'Alternativa D', 'E': 'Alternativa E'}),
        Questao(numero: 4, alternativas: {'A': 'Alternativa A', 'B': 'Alternativa B', 'C': 'Alternativa C', 'D': 'Alternativa D', 'E': 'Alternativa E'}),
        Questao(numero: 5, alternativas: {'A': 'Alternativa A', 'B': 'Alternativa B', 'C': 'Alternativa C', 'D': 'Alternativa D', 'E': 'Alternativa E'}),
      ];
    } else if (provaId == 201) {
      return [
        Questao(numero: 1, alternativas: {'A': 'Alternativa A', 'B': 'Alternativa B', 'C': 'Alternativa C', 'D': 'Alternativa D', 'E': 'Alternativa E'}),
        Questao(numero: 2, alternativas: {'A': 'Alternativa A', 'B': 'Alternativa B', 'C': 'Alternativa C', 'D': 'Alternativa D', 'E': 'Alternativa E'}),
      ];
    } else if (provaId == 2) {
      return [
        Questao(numero: 1, alternativas: {'A': 'Alternativa A', 'B': 'Alternativa B', 'C': 'Alternativa C', 'D': 'Alternativa D', 'E': 'Alternativa E'}),
        Questao(numero: 2, alternativas: {'A': 'Alternativa A', 'B': 'Alternativa B', 'C': 'Alternativa C', 'D': 'Alternativa D', 'E': 'Alternativa E'}),
        Questao(numero: 3, alternativas: {'A': 'Alternativa A', 'B': 'Alternativa B', 'C': 'Alternativa C', 'D': 'Alternativa D', 'E': 'Alternativa E'}),
        Questao(numero: 4, alternativas: {'A': 'Alternativa A', 'B': 'Alternativa B', 'C': 'Alternativa C', 'D': 'Alternativa D', 'E': 'Alternativa E'}),
        Questao(numero: 5, alternativas: {'A': 'Alternativa A', 'B': 'Alternativa B', 'C': 'Alternativa C', 'D': 'Alternativa D', 'E': 'Alternativa E'}),
      ];
    }
    return [];
  }
}