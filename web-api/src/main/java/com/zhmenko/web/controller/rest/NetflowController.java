package com.zhmenko.web.controller.rest;

import com.zhmenko.model.netflow.NetflowPacket;
import com.zhmenko.model.netflow.NetflowPacketV5;
import com.zhmenko.dao.BlackListDao;
import com.zhmenko.web.services.NetflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.web.bind.annotation.RestController
public class NetflowController {
    @Autowired
    NetflowService netflowService;
    @Autowired
    BlackListDao blackListDao;

    private static final String template = "Hello, %s!";

    public NetflowController(){
    }

    @GetMapping("/")
    public String def(){
        return "Hello world";
    }

    @GetMapping("/findByIp")
    public List<String> findPacketsByUserIp(String ip) throws URISyntaxException, SQLException {
        return netflowService.getByIp(ip)
                .stream()
                .map(NetflowPacket::toString)
                .collect(Collectors.toList());
    }

    /*@GetMapping(value = "/getCerificate", produces = { "application/json" })
    public ResponseEntity getFile(@RequestParam(value="key", required=false) String key, HttpServletRequest request) throws IOException {

        ResponseEntity respEntity = null;

        byte[] reportBytes = null;
        File result=new File("/home/arpit/Documents/PCAP/dummyPath/"+fileName);

        if(result.exists()){
            InputStream inputStream = new FileInputStream("/home/arpit/Documents/PCAP/dummyPath/"+fileName);
            String type=result.toURL().openConnection().guessContentTypeFromName(fileName);

            byte[] out=org.apache.commons.io.IOUtils.toByteArray(inputStream);

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add("content-disposition", "attachment; filename=" + fileName);
            responseHeaders.add("Content-Type",type);

            respEntity = new ResponseEntity(out, responseHeaders,HttpStatus.OK);
        }else{
            respEntity = new ResponseEntity ("File Not Found", HttpStatus.OK);
        }
        return respEntity;
    }*/

    /*@GetMapping("/getflows")
    public List<NetflowPacket> getAll(){
        return netflowService.getAll();
    }*/


    @PostMapping(value = "/save", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public void savePacket(@RequestBody NetflowPacketV5 netflowPacket){
        netflowService.save(netflowPacket);
    }

    @PostMapping("/saveList")
    public void savePacket(List<NetflowPacket> netflowPackets){
        netflowService.saveList(netflowPackets);
    }
}
