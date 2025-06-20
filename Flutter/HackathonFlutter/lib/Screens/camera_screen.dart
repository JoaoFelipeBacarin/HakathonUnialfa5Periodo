// lib/screens/camera_screen.dart
import 'package:camera/camera.dart';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'dart:io';
import '../services/ocr_service.dart';
import '../ui/widgets/msg_alerta.dart';
import 'package:hackathonflutter/screens/result_screen.dart'; // Importar a ResultScreen

class CameraScreen extends StatefulWidget {
  const CameraScreen({Key? key}) : super(key: key);

  @override
  State<CameraScreen> createState() => _CameraScreenState();
}

class _CameraScreenState extends State<CameraScreen> {
  CameraController? _controller;
  List<CameraDescription>? _cameras;
  XFile? _imageFile;
  final OcrService _ocrService = OcrService();
  bool _isProcessing = false;

  @override
  void initState() {
    super.initState();
    _initializeCamera();
  }

  Future<void> _initializeCamera() async {
    try {
      _cameras = await availableCameras();
      if (_cameras != null && _cameras!.isNotEmpty) {
        _controller = CameraController(_cameras![0], ResolutionPreset.high);
        await _controller!.initialize();
        if (!mounted) {
          return;
        }
        setState(() {});
      }
    } catch (e) {
      if (mounted) {
        MsgAlerta().show(
          context: context,
          titulo: 'Erro na Câmera',
          texto: 'Não foi possível inicializar a câmera: $e',
        );
      }
      print("Erro ao inicializar a câmera: $e");
    }
  }

  @override
  void dispose() {
    _controller?.dispose();
    super.dispose();
  }

  Future<void> _takePicture() async {
    if (!_controller!.value.isInitialized) {
      if (mounted) {
        MsgAlerta().show(
          context: context,
          titulo: 'Câmera não inicializada',
          texto: 'Aguarde a inicialização da câmera.',
        );
      }
      return;
    }

    try {
      setState(() {
        _isProcessing = true;
      });
      final XFile image = await _controller!.takePicture();
      _processImage(image);
    } catch (e) {
      if (mounted) {
        MsgAlerta().show(
          context: context,
          titulo: 'Erro ao Tirar Foto',
          texto: 'Não foi possível tirar a foto: $e',
        );
      }
      setState(() {
        _isProcessing = false;
      });
      print(e);
    }
  }

  Future<void> _pickImage() async {
    try {
      setState(() {
        _isProcessing = true;
      });
      final XFile? image = await ImagePicker().pickImage(source: ImageSource.gallery);
      if (image != null) {
        _processImage(image);
      } else {
        setState(() {
          _isProcessing = false;
        });
      }
    } catch (e) {
      if (mounted) {
        MsgAlerta().show(
          context: context,
          titulo: 'Erro ao Selecionar Imagem',
          texto: 'Não foi possível selecionar a imagem: $e',
        );
      }
      setState(() {
        _isProcessing = false;
      });
      print(e);
    }
  }

  Future<void> _processImage(XFile image) async {
    try {
      final Map<String, dynamic> extractedData = await _ocrService.processGabarito(image.path);

      if (mounted) {
        // Navegar para ResultScreen com os dados extraídos
        Navigator.pushReplacement(
          context,
          MaterialPageRoute(
            builder: (context) => ResultScreen(extractedData: extractedData),
          ),
        );
      }
    } catch (e) {
      if (mounted) {
        MsgAlerta().show(
          context: context,
          titulo: 'Erro no OCR',
          texto: 'Não foi possível processar a imagem: $e',
        );
      }
      print("Erro ao processar imagem com OCR: $e");
      setState(() {
        _isProcessing = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_controller == null || !_controller!.value.isInitialized) {
      return const Center(child: CircularProgressIndicator());
    }
    return Scaffold(
      appBar: AppBar(title: const Text('Escanear Gabarito')),
      body: Stack(
        children: [
          Column(
            children: [
              Expanded(
                child: _imageFile == null
                    ? CameraPreview(_controller!)
                    : Image.file(File(_imageFile!.path)),
              ),
              Padding(
                padding: const EdgeInsets.all(8.0),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  children: [
                    FloatingActionButton(
                      onPressed: _isProcessing ? null : _takePicture,
                      heroTag: 'takePicture',
                      child: const Icon(Icons.camera_alt),
                    ),
                    FloatingActionButton(
                      onPressed: _isProcessing ? null : _pickImage,
                      heroTag: 'pickImage',
                      child: const Icon(Icons.photo_library),
                    ),
                  ],
                ),
              ),
            ],
          ),
          if (_isProcessing)
            Container(
              color: Colors.black54,
              child: const Center(
                child: CircularProgressIndicator(),
              ),
            ),
        ],
      ),
    );
  }
}