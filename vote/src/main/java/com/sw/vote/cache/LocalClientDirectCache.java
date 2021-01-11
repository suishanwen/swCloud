package com.sw.vote.cache;//package com.sw.note.cache;
//
//import com.google.common.collect.Maps;
//import com.sw.note.model.entity.ClientDirect;
//import com.sw.note.util.ObjectUtil;
//
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//public class LocalClientDirectCache {
//
//    private static Map<String, ClientDirect> clientDirectMap = Maps.newConcurrentMap();
//    private static ObjectUtil objectUtil = new ObjectUtil();
//
//    public static void init(List<ClientDirect> clientDirectList) {
//        clientDirectList.forEach(clientDirect -> {
//            clientDirectMap.put(clientDirect.getId(), clientDirect);
//        });
//    }
//
//    public static ClientDirect get(String id) {
//        return clientDirectMap.get(id);
//    }
//
//    public static void setVal(String id, String field, String val) {
//        ClientDirect clientDirect = get(id);
//        objectUtil.setFieldValue(field, val, clientDirect);
//        clientDirect.set$synchronized(false);
//    }
//
//    public static void setValAll(String field, String val) {
//        clientDirectMap.values().forEach(clientDirect -> {
//            objectUtil.setFieldValue(field, val, clientDirect);
//            clientDirect.set$synchronized(false);
//        });
//    }
//
//    public static void put(ClientDirect clientDirect) {
//        clientDirect.set$synchronized(false);
//        clientDirectMap.put(clientDirect.getId(), clientDirect);
//    }
//
//    public static void remove(String id) {
//        clientDirectMap.remove(id);
//    }
//
//    public static List<ClientDirect> unSynchronized() {
//        return clientDirectMap.values().stream().filter(clientDirect -> !clientDirect.is$synchronized())
//                .collect(Collectors.toList());
//    }
//
//    public static List<ClientDirect> selectByUserId(String userId) {
//        String superUser = "root";
//        return clientDirectMap.values().stream().filter(clientDirect ->
//                superUser.equals(userId) || userId.equals(clientDirect.getUserId())
//        ).sorted((t1, t2) -> {
//            if (t1.getUserId().equals(t2.getUserId())) {
//                return t1.getSortNo() - t2.getSortNo();
//            } else {
//                return t1.getUserId().compareTo(t2.getUserId());
//            }
//        }).collect(Collectors.toList());
//    }
//
//    public static int versions() {
//        return Long.valueOf(clientDirectMap.values().stream().map(ClientDirect::getVersion).distinct().count()).intValue();
//    }
//
//    public static void upgrade() {
//        String version = clientDirectMap.values().stream().map(ClientDirect::getVersion).max(String::compareTo).orElse("");
//        clientDirectMap.values().stream().filter(clientDirect -> !version.equals(clientDirect.getVersion()))
//                .forEach(clientDirect -> {
//                    clientDirect.setDirect("TASK_SYS_UPDATE");
//                    clientDirect.set$synchronized(false);
//                });
//    }
//
//    public static double income(String userId) {
//        String superUser = "root";
//        return clientDirectMap.values().stream().filter(clientDirect ->
//                superUser.equals(userId) || userId.equals(clientDirect.getUserId())
//        ).mapToDouble(clientDirect -> Double.parseDouble(clientDirect.getReward())).sum();
//    }
//}
