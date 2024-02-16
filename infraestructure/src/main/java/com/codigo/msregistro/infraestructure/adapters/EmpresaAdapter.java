package com.codigo.msregistro.infraestructure.adapters;
import com.codigo.msregistro.domain.aggregates.constants.Constants;
import com.codigo.msregistro.domain.aggregates.dto.EmpresaDTO;
import com.codigo.msregistro.domain.aggregates.request.RequestEmpresa;
import com.codigo.msregistro.domain.aggregates.response.ResponseSunat;
import com.codigo.msregistro.domain.ports.out.EmpresaServiceOut;
import com.codigo.msregistro.infraestructure.entity.EmpresaEntity;
import com.codigo.msregistro.infraestructure.entity.TipoDocumentoEntity;
import com.codigo.msregistro.infraestructure.mapper.EmpresaMapper;
import com.codigo.msregistro.infraestructure.repository.EmpresaRepository;
import com.codigo.msregistro.infraestructure.repository.TipoDocumentoRepository;
import com.codigo.msregistro.infraestructure.rest.client.ClienteSunat;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmpresaAdapter implements EmpresaServiceOut {
    @Value("${token.api}")
    private String tokenApi;

    private final EmpresaRepository empresaRepository;
    private final EmpresaMapper empresaMapper;
    private final ClienteSunat clienteSunat;
    private final TipoDocumentoRepository tipoDocumentoRepository;
    @Override
    public EmpresaDTO crearEmpresaOut(RequestEmpresa requestEmpresa) {
        ResponseSunat datosSunat= getExecutionSunat(requestEmpresa.getNumDocu());
        empresaRepository.save(getEntity(datosSunat,requestEmpresa));
        return empresaMapper.mapToDto(getEntity(datosSunat,requestEmpresa));
    }

    @Override
    public Optional<EmpresaDTO> obtenerEmpresaOut(Long id) {

        return Optional.ofNullable(empresaMapper.mapToDto(empresaRepository.findById(id).get()));
    }

    @Override
    public List<EmpresaDTO> obtenerEmpresaTodosOut() {
        List<EmpresaDTO> empresaDTOList = new ArrayList<>();
        List<EmpresaEntity> entities=empresaRepository.findAll();
        for(EmpresaEntity empresa: entities){
            EmpresaDTO empresaDTO= empresaMapper.mapToDto(empresa);
            empresaDTOList.add(empresaDTO);

        }

        return empresaDTOList;
    }

    @Override
    public EmpresaDTO actualizarEmpresaOut(Long id, RequestEmpresa requestEmpresa) {
        boolean existe = empresaRepository.existsById(id);
        if(existe){
            Optional<EmpresaEntity> entity = empresaRepository.findById(id);
            ResponseSunat responseSunat = getExecutionSunat(requestEmpresa.getNumDocu());
            empresaRepository.save(getEntityUpdate(responseSunat,entity.get()));
            return empresaMapper.mapToDto(getEntityUpdate(responseSunat,entity.get()));
        }

        return  null;
    }

    @Override
    public EmpresaDTO deleteOut(Long id) {
        boolean existe = empresaRepository.existsById(id);
        if(existe){
            Optional<EmpresaEntity> entity = empresaRepository.findById(id);
            entity.get().setEstado(0);
            entity.get().setUsuaDelet(Constants.AUDIT_ADMIN);
            entity.get().setDateDelet(getTimestamp());
            empresaRepository.save(entity.get());
            return empresaMapper.mapToDto(entity.get());
        }
        return  null;
    }

    public ResponseSunat getExecutionSunat(String numero){
        String authorization = "Bearer "+tokenApi;
        ResponseSunat responseSunat = clienteSunat.getInfo(numero,authorization);
        return  responseSunat;
    }

    private EmpresaEntity getEntity(ResponseSunat sunat, RequestEmpresa requestEmpresa){
        TipoDocumentoEntity tipoDocumento = tipoDocumentoRepository.findByCodTipo("RUC");
        EmpresaEntity entity = new EmpresaEntity();
        entity.setNumDocu(sunat.getNumeroDocumento());
        entity.setNomComercial(sunat.getRazonSocial());
        entity.setRazonSocial(sunat.getRazonSocial());
        entity.setEstado(Constants.STATUS_ACTIVE);
        entity.setUsuaCrea(Constants.AUDIT_ADMIN);
        entity.setDateCreate(getTimestamp());
        entity.setTipoDocumento(tipoDocumento);
        return entity;
    }

    private Timestamp getTimestamp(){
        long currentTime = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(currentTime);
        return timestamp;
    }

    private EmpresaEntity getEntityUpdate(ResponseSunat sunat, EmpresaEntity empresaActualizar){
        empresaActualizar.setNumDocu(sunat.getNumeroDocumento());
        empresaActualizar.setNomComercial(sunat.getRazonSocial());
        empresaActualizar.setRazonSocial(sunat.getRazonSocial());
        empresaActualizar.setEstado(Constants.STATUS_ACTIVE);
        empresaActualizar.setUsuaCrea(Constants.AUDIT_ADMIN);
        empresaActualizar.setDateCreate(getTimestamp());
        return empresaActualizar;
    }
}
