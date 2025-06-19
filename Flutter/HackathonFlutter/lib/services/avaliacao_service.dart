// avaliacao_service.dart
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
    } else if (turmaId == 3) {
      return [
        Prova(id: 301, nome: 'Prova de Ciências', disciplina: 'Ciências', turmaId: 3),
      ];
    }
    return [];
  }

  // Modificado: Simula busca de questões para preenchimento pelo aluno
  Future<List<Questao>> buscarQuestoesPorProva(int provaId) async {
    await Future.delayed(const Duration(seconds: 1));

    // Retorna questões de exemplo APENAS com as letras das alternativas.
    // O texto completo da alternativa NÃO É RETORNADO NESTE FLUXO.
    return [
      Questao(
        numero: 1,
        alternativas: {
          'A': '', // Sem texto, apenas a letra será usada
          'B': '',
          'C': '',
          'D': '',
          'E': '', // Adicionado 'E' para consistência se necessário
        },
        respostaSelecionada: null,
      ),
      Questao(
        numero: 2,
        alternativas: {
          'A': '',
          'B': '',
          'C': '',
          'D': '',
          'E': '',
        },
        respostaSelecionada: null,
      ),
      Questao(
        numero: 3,
        alternativas: {
          'A': '',
          'B': '',
          'C': '',
          'D': '',
          'E': '',
        },
        respostaSelecionada: null,
      ),
      Questao(
        numero: 4,
        alternativas: {
          'A': '',
          'B': '',
          'C': '',
          'D': '',
          'E': '',
        },
        respostaSelecionada: null,
      ),
      Questao(
        numero: 5,
        alternativas: {
          'A': '',
          'B': '',
          'C': '',
          'D': '',
          'E': '',
        },
        respostaSelecionada: null,
      ),
    ];
  }

  // Modificado: Novo método para buscar o gabarito correto de uma prova
  // Retorna apenas a alternativa correta com o valor 'verdadeira'
  Future<List<Questao>> buscarGabaritoCorreto(int provaId) async {
    await Future.delayed(const Duration(seconds: 1));

    if (provaId == 101) { // Prova de Português
      return [
        Questao(
          numero: 1,
          alternativas: {
            'A': 'falsa',
            'B': 'verdadeira', // Apenas a 'B' é verdadeira
            'C': 'falsa',
            'D': 'falsa',
            'E': 'falsa',
          },
          respostaSelecionada: 'B', // Isso é para o gabarito_page saber qual marcar
        ),
        Questao(
          numero: 2,
          alternativas: {
            'A': 'falsa',
            'B': 'falsa',
            'C': 'verdadeira', // Apenas a 'C' é verdadeira
            'D': 'falsa',
            'E': 'falsa',
          },
          respostaSelecionada: 'C',
        ),
        Questao(
          numero: 3,
          alternativas: {
            'A': 'verdadeira', // Apenas a 'A' é verdadeira
            'B': 'falsa',
            'C': 'falsa',
            'D': 'falsa',
            'E': 'falsa',
          },
          respostaSelecionada: 'A',
        ),
        Questao(
          numero: 4,
          alternativas: {
            'A': 'falsa',
            'B': 'falsa',
            'C': 'falsa',
            'D': 'verdadeira', // Apenas a 'D' é verdadeira
            'E': 'falsa',
          },
          respostaSelecionada: 'D',
        ),
        Questao(
          numero: 5,
          alternativas: {
            'A': 'falsa',
            'B': 'verdadeira', // Apenas a 'B' é verdadeira
            'C': 'falsa',
            'D': 'falsa',
            'E': 'falsa',
          },
          respostaSelecionada: 'B',
        ),
      ];
    } else if (provaId == 102) { // Prova de Matemática
      return [
        Questao(
          numero: 1,
          alternativas: {
            'A': 'verdadeira', // Apenas a 'A' é verdadeira
            'B': 'falsa',
            'C': 'falsa',
            'D': 'falsa',
            'E': 'falsa',
          },
          respostaSelecionada: 'A',
        ),
        Questao(
          numero: 2,
          alternativas: {
            'A': 'falsa',
            'B': 'falsa',
            'C': 'falsa',
            'D': 'verdadeira', // Apenas a 'D' é verdadeira
            'E': 'falsa',
          },
          respostaSelecionada: 'D',
        ),
      ];
    }
    return [];
  }

  Future<void> enviarRespostasAluno(
      int alunoId, int provaId, List<Resposta> respostas) async {
    await Future.delayed(const Duration(seconds: 2));

    print('--- Respostas do Aluno $alunoId para a Prova $provaId ---');
    for (var resposta in respostas) {
      print('Questão ${resposta.questaoNumero}: Alternativa ${resposta.alternativaSelecionada}');
    }
    print('--------------------------------------------------');
  }
}