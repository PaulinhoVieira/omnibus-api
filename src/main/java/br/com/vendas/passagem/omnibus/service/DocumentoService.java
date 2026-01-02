package br.com.vendas.passagem.omnibus.service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import br.com.vendas.passagem.omnibus.domain.Documento;
import br.com.vendas.passagem.omnibus.domain.Usuario;
import br.com.vendas.passagem.omnibus.domain.enums.TipoDocumento;
import br.com.vendas.passagem.omnibus.dto.response.DocumentoResponseDTO;
import br.com.vendas.passagem.omnibus.repository.DocumentoRepository;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;

@Service
public class DocumentoService {

    private final DocumentoRepository documentoRepository;
    private final UsuarioService usuarioService;
    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    public DocumentoService(DocumentoRepository documentoRepository, UsuarioService usuarioService, MinioClient minioClient) {
        this.documentoRepository = documentoRepository;
        this.usuarioService = usuarioService;
        this.minioClient = minioClient;
    }

    @Transactional
    public DocumentoResponseDTO uploadDocumento(Long usuarioId, TipoDocumento tipoDocumento, MultipartFile arquivo) {
        try {
            ensureBucket();

            Usuario usuario = usuarioService.obterPorId(usuarioId);
            String objectName = gerarNomeObjeto(usuarioId, tipoDocumento, arquivo.getOriginalFilename());

            try (InputStream is = arquivo.getInputStream()) {
                minioClient.putObject(
                    PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(is, arquivo.getSize(), -1)
                        .contentType(arquivo.getContentType())
                        .build()
                );
            }

            Documento documento = new Documento();
            documento.setUsuario(usuario);
            documento.setTipo(tipoDocumento);
            documento.setNomeArquivoMinio(objectName);
            documento.setContentType(arquivo.getContentType());
            documento.setDataUpload(LocalDateTime.now());

            Documento salvo = documentoRepository.save(documento);
            return new DocumentoResponseDTO(salvo.getId(), usuario.getId(), salvo.getTipo(), salvo.getNomeArquivoMinio(), salvo.getContentType());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao fazer upload do documento", e);
        }
    }

    private void ensureBucket() throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    private String gerarNomeObjeto(Long usuarioId, TipoDocumento tipoDocumento, String originalFilename) {
        String sanitized = originalFilename == null ? "arquivo" : originalFilename.replaceAll("\\s+", "_");
        return "usuarios/" + usuarioId + "/" + tipoDocumento.name().toLowerCase() + "/" + UUID.randomUUID() + "-" + sanitized;
    }
}
