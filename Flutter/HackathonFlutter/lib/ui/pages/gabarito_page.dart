// gabarito_page.dart
import 'package:flutter/material.dart';
import 'package:hackathonflutter/models/aluno.dart';
import 'package:hackathonflutter/models/questao.dart';
import 'package:hackathonflutter/models/resposta.dart';
import 'package:hackathonflutter/models/prova.dart';
import 'package:hackathonflutter/services/avaliacao_service.dart';
import 'package:hackathonflutter/ui/widgets/circulo_espera.dart';
import 'package:hackathonflutter/ui/widgets/msg_alerta.dart';
import 'package:hackathonflutter/ui/widgets/botao_flutuante.dart';

class GabaritoPage extends StatefulWidget {
  final Aluno? aluno;
  final Prova prova;

  const GabaritoPage({
    super.key,
    required this.aluno,
    required this.prova,
  }) : assert(aluno != null, 'Aluno deve ser fornecido para lançamento de gabarito.');

  const GabaritoPage.forGabarito({
    super.key,
    required this.prova,
  }) : aluno = null;

  @override
  State<GabaritoPage> createState() => _GabaritoPageState();
}

class _GabaritoPageState extends State<GabaritoPage> {
  final AvaliacaoService _avaliacaoService = AvaliacaoService();
  List<Questao> _questoes = [];
  bool _carregando = true;
  bool _enviando = false;
  bool _lendoCamera = false;
  late bool _isViewingGabaritoCorreto;

  @override
  void initState() {
    super.initState();
    _isViewingGabaritoCorreto = widget.aluno == null;
    _carregarQuestoes();
  }

  Future<void> _carregarQuestoes() async {
    setState(() {
      _carregando = true;
    });

    try {
      if (_isViewingGabaritoCorreto) {
        _questoes = await _avaliacaoService.buscarGabaritoCorreto(widget.prova.id);
      } else {
        _questoes = await _avaliacaoService.buscarQuestoesPorProva(widget.prova.id);
      }
      setState(() {
        _carregando = false;
      });
    } catch (e) {
      if (mounted) {
        MsgAlerta().show(
          context: context,
          titulo: 'Erro',
          texto: 'Erro ao carregar questões: $e',
        );
        setState(() {
          _carregando = false;
        });
      }
    }
  }

  void _selecionarResposta(int numeroQuestao, String resposta) {
    if (_isViewingGabaritoCorreto) return;

    setState(() {
      final questaoIndex = _questoes.indexWhere((q) => q.numero == numeroQuestao);
      if (questaoIndex != -1) {
        _questoes[questaoIndex].respostaSelecionada = resposta;
      }
    });
  }

  Future<void> _enviarRespostas() async {
    if (_isViewingGabaritoCorreto) return;

    setState(() {
      _enviando = true;
    });

    try {
      final List<Resposta> respostasDoAluno = _questoes
          .where((q) => q.respostaSelecionada != null)
          .map((q) => Resposta(
        alunoId: widget.aluno!.id,
        questaoNumero: q.numero,
        alternativaSelecionada: q.respostaSelecionada!,
        dataResposta: DateTime.now(),
      ))
          .toList();

      if (respostasDoAluno.isEmpty) {
        if (mounted) {
          MsgAlerta().show(
            context: context,
            titulo: 'Atenção',
            texto: 'Por favor, selecione pelo menos uma resposta antes de enviar.',
          );
        }
        setState(() {
          _enviando = false;
        });
        return;
      }

      await _avaliacaoService.enviarRespostasAluno(
        widget.aluno!.id,
        widget.prova.id,
        respostasDoAluno,
      );

      if (mounted) {
        MsgAlerta().show(
          context: context,
          titulo: 'Sucesso',
          texto: 'Respostas enviadas com sucesso!',
        ).then((_) {
          Navigator.pop(context);
        });
      }
    } catch (e) {
      if (mounted) {
        MsgAlerta().show(
          context: context,
          titulo: 'Erro',
          texto: 'Erro ao enviar respostas: $e',
        );
      }
    } finally {
      setState(() {
        _enviando = false;
      });
    }
  }

  void _lerGabaritoCamera() {
    if (_isViewingGabaritoCorreto) return;

    setState(() {
      _lendoCamera = true;
    });
    Future.delayed(const Duration(seconds: 2), () {
      setState(() {
        _lendoCamera = false;
        MsgAlerta().show(
          context: context,
          titulo: 'Câmera',
          texto: 'Funcionalidade de câmera ainda não implementada para este fluxo.',
        );
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(
          _isViewingGabaritoCorreto
              ? 'Gabarito da Prova: ${widget.prova.nome}'
              : 'Preencher Gabarito: ${widget.aluno?.nome ?? 'N/A'} - Prova: ${widget.prova.nome}',
        ),
      ),
      body: _carregando
          ? const CirculoEspera()
          : Column(
        children: [
          Expanded(
            child: ListView.builder(
              padding: const EdgeInsets.all(16.0),
              itemCount: _questoes.length,
              itemBuilder: (context, index) {
                final questao = _questoes[index];
                return Card(
                  margin: const EdgeInsets.symmetric(vertical: 8.0),
                  elevation: 2,
                  child: Padding(
                    padding: const EdgeInsets.all(16.0),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          'Questão ${questao.numero}',
                          style: const TextStyle(
                            fontSize: 18,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                        const SizedBox(height: 10),
                        // Exibe as alternativas
                        // Usamos Column para garantir que estejam na vertical
                        Column(
                          children: questao.alternativas.keys.map((key) {
                            // Determina se esta alternativa é a correta (no gabarito oficial)
                            // ou a selecionada pelo aluno (no modo de preenchimento)
                            final isCorrectOrSelected = _isViewingGabaritoCorreto
                                ? questao.alternativas[key] == 'verdadeira'
                                : questao.respostaSelecionada == key;

                            if (_isViewingGabaritoCorreto) {
                              // Modo "Ver Gabarito"
                              return Padding(
                                padding: const EdgeInsets.symmetric(vertical: 4.0),
                                child: Row(
                                  children: [
                                    Icon(
                                      isCorrectOrSelected ? Icons.check_circle : Icons.cancel,
                                      color: isCorrectOrSelected ? Colors.green : Colors.red,
                                    ),
                                    const SizedBox(width: 8),
                                    Text(
                                      '$key) ${isCorrectOrSelected ? 'Verdadeira' : 'Falsa'}',
                                      style: TextStyle(
                                        fontSize: 16,
                                        fontWeight: isCorrectOrSelected ? FontWeight.bold : FontWeight.normal,
                                        color: isCorrectOrSelected ? Colors.green : Colors.red,
                                      ),
                                    ),
                                  ],
                                ),
                              );
                            } else {
                              // Modo "Selecionar Aluno" (Preencher Gabarito)
                              return RadioListTile<String>(
                                title: Text('$key'), // Exibe apenas a letra (A, B, C, D, E)
                                value: key,
                                groupValue: questao.respostaSelecionada,
                                onChanged: _carregando || _enviando
                                    ? null
                                    : (String? value) {
                                  if (value != null) {
                                    _selecionarResposta(questao.numero, value);
                                  }
                                },
                              );
                            }
                          }).toList(),
                        ),
                      ],
                    ),
                  ),
                );
              },
            ),
          ),
          if (!_isViewingGabaritoCorreto)
            Padding(
              padding: const EdgeInsets.all(16.0),
              child: _enviando
                  ? const CirculoEspera()
                  : ElevatedButton.icon(
                onPressed: _enviarRespostas,
                icon: const Icon(Icons.send),
                label: const Text('Enviar Respostas'),
                style: ElevatedButton.styleFrom(
                  minimumSize: const Size(double.infinity, 50),
                ),
              ),
            ),
          if (!_isViewingGabaritoCorreto)
            Align(
              alignment: Alignment.bottomRight,
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: BotaoFlutuante(
                  icone: Icons.camera_alt,
                  evento: _lerGabaritoCamera,
                ),
              ),
            ),
        ],
      ),
    );
  }
}