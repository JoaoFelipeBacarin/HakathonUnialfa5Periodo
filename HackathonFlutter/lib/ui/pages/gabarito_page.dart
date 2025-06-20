// lib/ui/pages/gabarito_page.dart
import 'package:flutter/material.dart';
import 'package:hackathonflutter/models/aluno.dart';
import 'package:hackathonflutter/models/questao.dart';
import 'package:hackathonflutter/models/resposta.dart';
import 'package:hackathonflutter/models/prova.dart';
import 'package:hackathonflutter/services/avaliacao_service.dart';
import 'package:hackathonflutter/services/ocr_service.dart'; // Import do OcrService
import 'package:hackathonflutter/ui/widgets/circulo_espera.dart';
import 'package:hackathonflutter/ui/widgets/msg_alerta.dart';
import 'package:hackathonflutter/ui/widgets/botao_flutuante.dart';
import 'package:hackathonflutter/screens/camera_screen.dart'; // Importar a CameraScreen

class GabaritoPage extends StatefulWidget {
  final Aluno? aluno; // Pode ser nulo se for visualização de gabarito
  final Prova prova; // Sempre será necessário, seja para aluno ou gabarito
  final bool isViewingGabarito; // Controla se é apenas visualização do gabarito correto (tornada pública)

  // Construtor principal para lançar gabarito de um aluno específico
  const GabaritoPage({
    super.key,
    required this.aluno, // Aluno é requerido para este construtor
    required this.prova,
  }) : isViewingGabarito = false, // Define explicitamente para false para lançamento de gabarito
        assert(aluno != null, 'Aluno deve ser fornecido para lançamento de gabarito.'); // Garante que aluno não é nulo em modo debug

  // Construtor nomeado para visualização do gabarito correto da prova (sem aluno)
  const GabaritoPage.forGabarito({
    super.key,
    this.aluno, // Aluno pode ser nulo para este construtor
    required this.prova,
  }) : isViewingGabarito = true; // Define explicitamente para true para visualização

  @override
  State<GabaritoPage> createState() => _GabaritoPageState();
}

class _GabaritoPageState extends State<GabaritoPage> {
  final AvaliacaoService _avaliacaoService = AvaliacaoService();
  final OcrService _ocrService = OcrService(); // Instância do OCR Service
  List<Questao> _questoes = [];
  bool _carregando = false;
  bool _enviando = false;

  @override
  void initState() {
    super.initState();
    _carregarGabarito();
  }

  Future<void> _carregarGabarito() async {
    if (!mounted) return; // Garante que o widget ainda está montado
    setState(() {
      _carregando = true;
    });

    try {
      if (widget.isViewingGabarito) {
        // Se for para visualizar o gabarito correto da prova
        _questoes = await _avaliacaoService.buscarGabaritoOficial(widget.prova.id);
      } else {
        // Se for para lançar/editar gabarito de um aluno
        // **VERIFICAÇÃO CRÍTICA AQUI PARA EVITAR NULL EXCEPTION**
        if (widget.aluno == null) {
          if (mounted) {
            MsgAlerta().show(
              context: context,
              titulo: 'Erro de Dados',
              texto: 'Dados do aluno estão ausentes para lançar ou editar o gabarito. Por favor, selecione o aluno novamente.',
            ).then((_) {
              // Opcional: Navegar de volta ou tomar outra ação após o alerta
              Navigator.pop(context); // Exemplo: volta para a tela anterior
            });
          }
          return; // Interrompe a execução para evitar o erro.
        }

        // Agora, sabemos que widget.aluno não é nulo.
        final int alunoId = widget.aluno!.id;
        final int provaId = widget.prova.id;

        // Primeiro busca o gabarito oficial como base
        List<Questao> gabaritoOficial = await _avaliacaoService.buscarGabaritoOficial(provaId);

        // Em seguida, busca as respostas do aluno e as aplica ao gabarito
        List<Resposta> respostasAluno = await _avaliacaoService.buscarRespostasAluno(alunoId, provaId);

        // Mapeia as respostas do aluno para as questões do gabarito oficial
        _questoes = gabaritoOficial.map((questaoOficial) {
          final respostaAluno = respostasAluno.firstWhere(
                (resp) => resp.questaoNumero == questaoOficial.numero,
            orElse: () => Resposta(
              alunoId: alunoId,
              questaoNumero: questaoOficial.numero,
              alternativaSelecionada: '', // Resposta vazia se não encontrada
              dataResposta: DateTime.now(),
            ),
          );
          return Questao(
            numero: questaoOficial.numero,
            alternativas: questaoOficial.alternativas,
            respostaSelecionada: respostaAluno.alternativaSelecionada.isNotEmpty
                ? respostaAluno.alternativaSelecionada
                : null, // Usa null se a resposta estiver vazia
          );
        }).toList();
      }
    } catch (e) {
      if (mounted) {
        MsgAlerta().show(
          context: context,
          titulo: 'Erro',
          texto: 'Não foi possível carregar o gabarito: $e',
        );
      }
    } finally {
      if (mounted) {
        setState(() {
          _carregando = false;
        });
      }
    }
  }

  void _selecionarAlternativa(int questaoNumero, String alternativa) {
    if (widget.isViewingGabarito) {
      // Se estiver apenas visualizando o gabarito, não permite alterações
      return;
    }
    setState(() {
      final questaoIndex = _questoes.indexWhere((q) => q.numero == questaoNumero);
      if (questaoIndex != -1) {
        // Se a alternativa selecionada for a mesma, desmarca. Caso contrário, marca a nova.
        if (_questoes[questaoIndex].respostaSelecionada == alternativa) {
          _questoes[questaoIndex].respostaSelecionada = null;
        } else {
          _questoes[questaoIndex].respostaSelecionada = alternativa;
        }
      }
    });
  }

  Future<void> _enviarRespostas() async {
    if (widget.aluno == null) {
      MsgAlerta().show(
        context: context,
        titulo: 'Erro',
        texto: 'Não é possível enviar respostas sem um aluno selecionado. Por favor, volte e selecione um aluno.',
      );
      return;
    }

    setState(() {
      _enviando = true;
    });

    try {
      // Filtra apenas as questões que foram respondidas
      final respostasParaEnviar = _questoes
          .where((q) => q.respostaSelecionada != null && q.respostaSelecionada!.isNotEmpty)
          .map((q) => Resposta(
        alunoId: widget.aluno!.id,
        questaoNumero: q.numero,
        alternativaSelecionada: q.respostaSelecionada!,
        dataResposta: DateTime.now(), // Adiciona o timestamp
      ))
          .toList();

      if (respostasParaEnviar.isEmpty) {
        if (mounted) {
          MsgAlerta().show(
            context: context,
            titulo: 'Atenção',
            texto: 'Nenhuma resposta foi selecionada para envio.',
          );
        }
        return;
      }

      await _avaliacaoService.enviarRespostasAluno(
        widget.aluno!.id,
        widget.prova.id,
        respostasParaEnviar,
      );

      if (mounted) {
        MsgAlerta().show(
          context: context,
          titulo: 'Sucesso',
          texto: 'Gabarito enviado com sucesso!',
        ).then((_) {
          Navigator.pop(context, true); // Retorna 'true' indicando sucesso
        });
      }
    } catch (e) {
      if (mounted) {
        MsgAlerta().show(
          context: context,
          titulo: 'Erro',
          texto: 'Falha ao enviar gabarito: $e',
        );
      }
    } finally {
      if (mounted) {
        setState(() {
          _enviando = false;
        });
      }
    }
  }

  Future<void> _lerGabaritoCamera() async {
    if (widget.aluno == null) {
      MsgAlerta().show(
        context: context,
        titulo: 'Erro',
        texto: 'Selecione um aluno antes de ler o gabarito pela câmera.',
      );
      return;
    }

    // Navega para a CameraScreen e espera o resultado
    final result = await Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => const CameraScreen()),
    );

    if (result != null && result is Map<String, dynamic>) {
      // Processar os dados extraídos pelo OCR
      final extractedAlunoId = int.tryParse(result['alunoId'] ?? '');
      final extractedProvaId = int.tryParse(result['provaId'] ?? '');
      final extractedRespostas = List<Map<String, String>>.from(result['respostas'] ?? []);

      // Validação básica para garantir que os IDs correspondem
      if (extractedAlunoId != widget.aluno!.id || extractedProvaId != widget.prova.id) {
        if (mounted) {
          MsgAlerta().show(
            context: context,
            titulo: 'Dados Inconsistentes',
            texto: 'Os dados escaneados não correspondem ao aluno ou prova selecionados. '
                'Aluno Escaneado: $extractedAlunoId, Prova Escaneada: $extractedProvaId. '
                'Aluno Selecionado: ${widget.aluno!.id}, Prova Selecionada: ${widget.prova.id}.',
          );
        }
        return;
      }

      // Aplicar as respostas extraídas do OCR ao gabarito atual
      setState(() {
        for (var extractedResp in extractedRespostas) {
          final questaoNumero = int.tryParse(extractedResp['questao']!);
          final alternativa = extractedResp['resposta'];

          if (questaoNumero != null && alternativa != null && alternativa.isNotEmpty) {
            final questaoIndex = _questoes.indexWhere((q) => q.numero == questaoNumero);
            if (questaoIndex != -1) {
              // Verifica se a alternativa extraída é uma das opções válidas para a questão
              if (_questoes[questaoIndex].alternativas.keys.contains(alternativa)) {
                _questoes[questaoIndex].respostaSelecionada = alternativa;
              } else {
                // Opcional: Lidar com alternativas inválidas (ex: ignorar, ou alertar o usuário)
                print('Aviso: Alternativa "$alternativa" para questão $questaoNumero é inválida.');
              }
            }
          }
        }
      });

      if (mounted) {
        MsgAlerta().show(
          context: context,
          titulo: 'Sucesso OCR',
          texto: 'Respostas do gabarito aplicadas com sucesso a partir da câmera!',
        );
      }
    } else if (result == null) {
      if (mounted) {
        MsgAlerta().show(
          context: context,
          titulo: 'Operação Cancelada',
          texto: 'Leitura do gabarito pela câmera foi cancelada ou não retornou dados.',
        );
      }
    } else {
      if (mounted) {
        MsgAlerta().show(
          context: context,
          titulo: 'Erro OCR',
          texto: 'Ocorreu um erro ao processar a imagem da câmera ou os dados retornados são inválidos.',
        );
      }
    }
  }


  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.isViewingGabarito
            ? 'Gabarito Oficial da Prova ${widget.prova.nome}'
            : 'Gabarito do Aluno: ${widget.aluno?.nome ?? 'N/A'} - ${widget.prova.nome}'),
      ),
      body: _carregando
          ? const CirculoEspera()
          : Stack(
        children: [
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: ListView.builder(
              itemCount: _questoes.length,
              itemBuilder: (context, index) {
                final questao = _questoes[index];
                return Card(
                  margin: const EdgeInsets.only(bottom: 16.0),
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
                        Column(
                          children: questao.alternativas.entries.map((entry) {
                            final alternativaLetra = entry.key;
                            final alternativaTexto = entry.value;
                            final isSelected =
                                questao.respostaSelecionada == alternativaLetra;

                            return RadioListTile<String>(
                              title: Text('$alternativaLetra) $alternativaTexto'),
                              value: alternativaLetra,
                              groupValue: questao.respostaSelecionada,
                              onChanged: (widget.isViewingGabarito)
                                  ? null // Desabilita se for visualização
                                  : (value) {
                                _selecionarAlternativa(questao.numero, value!);
                              },
                              activeColor: widget.isViewingGabarito
                                  ? Colors.green // Verde para gabarito correto
                                  : Theme.of(context).primaryColor, // Cor padrão para seleção
                            );
                          }).toList(),
                        ),
                      ],
                    ),
                  ),
                );
              },
            ),
          ),
          if (!widget.isViewingGabarito) // Botão de enviar só aparece se não for gabarito correto
            Align(
              alignment: Alignment.bottomCenter,
              child: Padding(
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
            ),
          if (!widget.isViewingGabarito) // Botão flutuante da câmera só aparece se não for gabarito correto
            Align(
              alignment: Alignment.bottomRight,
              child: Padding(
                // Ajuste a posição para não cobrir o botão "Enviar"
                padding: const EdgeInsets.only(bottom: 90.0, right: 16.0),
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