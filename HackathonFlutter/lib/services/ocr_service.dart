import 'package:google_mlkit_text_recognition/google_mlkit_text_recognition.dart';
import 'package:image/image.dart' as img; // Manter, caso seja usado para pré-processamento futuro
import 'dart:io'; // Manter

class OcrService {
  final TextRecognizer _textRecognizer = TextRecognizer();

  Future<Map<String, dynamic>> processGabarito(String imagePath) async {
    final inputImage = InputImage.fromFilePath(imagePath);
    final RecognizedText recognizedText = await _textRecognizer.processImage(inputImage);

    print('Recognized Text (Full):');
    for (TextBlock block in recognizedText.blocks) {
      print('  Block: ${block.text}');
      for (TextLine line in block.lines) {
        print('    Line: ${line.text}');
        // Optional: print individual elements for more detail to understand spatial data
        for (TextElement element in line.elements) {
          print('      Element: ${element.text}, Bounds: ${element.boundingBox}');
        }
      }
    }

    Map<String, dynamic> extractedData = {
      "alunoId": "",
      "provaId": "",
      "respostas": [],
      "metadados": {
        "timestamp": DateTime.now().toIso8601String(),
        "qualidade_ocr": 0.0 // Placeholder
      }
    };

    // --- Nova Lógica Espacial para Extração de Aluno ID e Prova ID ---
    String? alunoId;
    String? provaId;

    // Coletar todos os elementos com suas posições
    List<Map<String, dynamic>> allElements = [];

    for (TextBlock block in recognizedText.blocks) {
      for (TextLine line in block.lines) {
        for (TextElement element in line.elements) {
          allElements.add({
            'text': element.text,
            'bounds': element.boundingBox,
            'centerX': (element.boundingBox.left + element.boundingBox.right) / 2,
            'centerY': (element.boundingBox.top + element.boundingBox.bottom) / 2,
          });
        }
      }
    }

    // Procurar por "Aluno" e encontrar o número mais próximo à direita
    for (var element in allElements) {
      if (element['text'].toLowerCase() == 'aluno') {
        double alunoY = element['centerY'];
        double alunoRight = element['bounds'].right;

        // Procurar por números na mesma linha (±20 pixels) e à direita
        for (var candidate in allElements) {
          if (RegExp(r'^\d+$').hasMatch(candidate['text'])) {
            double candidateY = candidate['centerY'];
            double candidateLeft = candidate['bounds'].left;

            // Verificar se está na mesma linha (±20 pixels) e à direita
            if ((candidateY - alunoY).abs() <= 20 && candidateLeft > alunoRight) {
              alunoId = candidate['text'];
              break;
            }
          }
        }
        if (alunoId != null) break;
      }
    }

    // Procurar por "Prova" e encontrar o número mais próximo à direita
    for (var element in allElements) {
      if (element['text'].toLowerCase() == 'prova') {
        double provaY = element['centerY'];
        double provaRight = element['bounds'].right;

        // Procurar por números na mesma linha (±20 pixels) e à direita
        for (var candidate in allElements) {
          if (RegExp(r'^\d+$').hasMatch(candidate['text'])) {
            double candidateY = candidate['centerY'];
            double candidateLeft = candidate['bounds'].left;

            // Verificar se está na mesma linha (±20 pixels) e à direita
            if ((candidateY - provaY).abs() <= 20 && candidateLeft > provaRight) {
              provaId = candidate['text'];
              break;
            }
          }
        }
        if (provaId != null) break;
      }
    }

    // Definir os IDs extraídos
    if (alunoId != null) {
      extractedData["alunoId"] = alunoId;
    }
    if (provaId != null) {
      extractedData["provaId"] = provaId;
    }
    // --- Fim da Nova Lógica Espacial ---

    // Lógica existente para extrair respostas
    final Map<int, String> uniqueAnswers = {};
    final answerPattern = RegExp(r"(\d+)\s*([A-Ea-e])"); // Ex: "1A", "2 C", "3E"

    for (TextBlock block in recognizedText.blocks) {
      for (TextLine line in block.lines) {
        final matches = answerPattern.allMatches(line.text);
        for (var match in matches) {
          int? qNum = int.tryParse(match.group(1)!);
          String? answer = match.group(2)!.toUpperCase();
          if (qNum != null && answer != null) {
            uniqueAnswers[qNum] = answer;
          }
        }
      }
    }

    // Caso o padrão principal não capture tudo (provável para gabaritos de bolha),
    // podemos tentar uma regex de fallback mais simples que procura por (Número) (Letra)
    // em qualquer parte de uma linha, ignorando pontuações e outros textos ao redor.
    if (uniqueAnswers.isEmpty) {
      final fallbackAnswerPattern = RegExp(r"(\d+)\s*([A-E])", caseSensitive: false);
      for (TextBlock block in recognizedText.blocks) {
        for (TextLine line in block.lines) {
          final matches = fallbackAnswerPattern.allMatches(line.text);
          for (var match in matches) {
            int? qNum = int.tryParse(match.group(1)!);
            String? answer = match.group(2)!.toUpperCase();
            if (qNum != null && answer != null) {
              uniqueAnswers[qNum] = answer;
            }
          }
        }
      }
    }

    List<Map<String, String>> respostas = [];
    uniqueAnswers.forEach((key, value) {
      respostas.add({"questao": key.toString(), "resposta": value});
    });

    // Sort answers by question number for consistency
    respostas.sort((a, b) => int.parse(a["questao"]!).compareTo(int.parse(b["questao"]!)));

    extractedData["respostas"] = respostas;

    print('Extracted Data (after parsing):');
    print('  Aluno ID: ${extractedData["alunoId"]}');
    print('  Prova ID: ${extractedData["provaId"]}');
    print('  Respostas: ${extractedData["respostas"]}');

    return extractedData;
  }

  void dispose() {
    _textRecognizer.close();
  }
}