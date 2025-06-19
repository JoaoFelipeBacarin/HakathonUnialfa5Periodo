import 'package:google_mlkit_text_recognition/google_mlkit_text_recognition.dart';
import 'package:image/image.dart' as img;
import 'dart:io';

class OcrService {
  final TextRecognizer _textRecognizer = TextRecognizer(); // Removido 'script: TextScript.latin'

  Future<Map<String, dynamic>> processGabarito(String imagePath) async {
    final inputImage = InputImage.fromFilePath(imagePath);
    final RecognizedText recognizedText = await _textRecognizer.processImage(inputImage);

    print('Recognized Text:');
    for (TextBlock block in recognizedText.blocks) {
      print('  Block: ${block.text}');
      for (TextLine line in block.lines) {
        print('    Line: ${line.text}');
        for (TextElement element in line.elements) {
          print('      Element: ${element.text}');
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

    // Simplified logic to extract student ID and exam ID
    for (TextBlock block in recognizedText.blocks) {
      if (block.text.toLowerCase().contains("aluno")) {
        final match = RegExp(r"aluno(?:\s*ID)?:\s*(\w+)", caseSensitive: false).firstMatch(block.text);
        if (match != null) {
          extractedData["alunoId"] = match.group(1);
        }
      }
      if (block.text.toLowerCase().contains("prova")) {
        final match = RegExp(r"prova(?:\s*ID)?:\s*(\w+)", caseSensitive: false).firstMatch(block.text);
        if (match != null) {
          extractedData["provaId"] = match.group(1);
        }
      }
    }

    // Simplified logic to extract answers based on common patterns
    // This is a very basic heuristic and will likely need refinement
    // for real-world gabaritos.
    List<Map<String, String>> respostas = [];
    for (TextBlock block in recognizedText.blocks) {
      for (TextLine line in block.lines) {
        // Look for patterns like "1. A", "2. B", "3. C"
        final RegExp answerPattern = RegExp(r"^(\d+)\.\s*([A-E])", caseSensitive: false);
        final match = answerPattern.firstMatch(line.text.trim());
        if (match != null) {
          respostas.add({
            "questao": match.group(1)!,
            "resposta": match.group(2)!.toUpperCase(),
          });
        }
      }
    }
    extractedData["respostas"] = respostas;

    return extractedData;
  }

  void dispose() {
    _textRecognizer.close();
  }
}


