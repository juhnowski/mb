package ru.simplgroupp.rest.api;

import org.apache.commons.lang.StringUtils;
import ru.simplgroupp.fias.ejb.IFIASService;
import ru.simplgroupp.fias.persistence.AddrObj;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.util.*;

/**
 * RESTful функции для работы с адресами
 *
 * @autor Andrey Unger aka Cobalt <unger1984@gmail.com> http://www.servicepro-online.ru
 * @since  22.04.14.
 */
@Stateless
@Path("/fias")
public class FiasController {
    private final static int PAGING_SIZE = 20;

    @EJB
    protected IFIASService fiasBean;

    public static final class AddressRest implements Serializable {
        private String id;

        private String label;

        private String levelName;

        public static List<AddressRest> toAddressRestList(List<AddrObj> addressObjectList) {
            List<AddressRest> restList = new ArrayList(addressObjectList.size());
            for(AddrObj addressObject: addressObjectList) {
                restList.add(new AddressRest(addressObject.getAOGUID(), addressObject.getOfficialName() + " " + addressObject.getType().getName().toLowerCase(),
                        addressObject.getType().getName()));
            }
            return restList;
        }

        private AddressRest(String id, String label) {
            this.id = id;
            this.label = label;
        }


        private AddressRest(String id, String label, String levelName) {
            this.id = id;
            this.label = label;
            this.levelName = levelName;
        }


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }


        public String getLevelName() {
            return levelName;
        }


        public void setLevelName(String levelName) {
            this.levelName = levelName;
        }
    }

    @GET
    @Path("/")
    public JsonPartialResult<AddressRest> asyncListLevel(@QueryParam("term") String term, @QueryParam("page") Integer page, @QueryParam("level") int level, @QueryParam("parent") String parentGUID) {
        List<AddrObj> addressObjectList = fiasBean.listAddrObj(level == 1 ? level : null, null, parentGUID, term, page, PAGING_SIZE, true);
        return new JsonPartialResult<>(AddressRest.toAddressRestList(addressObjectList), addressObjectList.size() == PAGING_SIZE);
    }

    @GET
    @Path("/hasChildren")
    public JsonResult<Boolean> hasChidreb(@QueryParam("parent") String parentGUID) {
        JsonResult result = new JsonResult();
        result.setData(fiasBean.hasChildren(parentGUID, true));
        return result;
    }

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response listLevel(@QueryParam("level") int level, @QueryParam("parent") String parentGUID) {
        List<AddrObj> addressObjectList = fiasBean.listAddrObj(level == 1 ? level : null, null, parentGUID, true);
        GenericEntity<List<AddressRest>> entity = new GenericEntity<List<AddressRest>>(AddressRest.toAddressRestList(addressObjectList)) {};
        return Response.ok(entity).build();
    }

    /**
     * Возвращает из ФИАС список регионов
     * @return
     */
    @POST
    @Path("/regions")
    @Produces(MediaType.TEXT_HTML + ";charset=utf-8")
    public String getRegions() {
        List<AddrObj> list = fiasBean.listRegions(Boolean.TRUE);
        return getSortFIASList(list);
    }

    /**
     * Возвращает из ФИАС список районов региона
     * @param data 0 - регион
     * @return
     */
    @POST
    @Path("/area")
    @Consumes("application/json")
    @Produces(MediaType.TEXT_HTML + ";charset=utf-8")
    public String getRaions(ArrayList<String> data) {
        if (data.size() == 1) {
            String guid = data.get(0);
            List<AddrObj> list = fiasBean.listAddrObj(ru.simplgroupp.fias.persistence.Level.RAION.getID(), null, guid, Boolean.TRUE);
            return "{\"status\":\"ok\",\"answer\":\""+getSortFIASList(list)+"\"}";
        }
        return "{\"status\":\"ok\",\"answer\":\"\"}";
    }

    /**
     * Возвращает из ФИАС список городов региона или района
     * @param data 0 - регион, 1 - район
     * @return
     */
    @POST
    @Path("/city")
    @Consumes("application/json")
    @Produces(MediaType.TEXT_HTML + ";charset=utf-8")
    public String getCity(ArrayList<String> data) {
        if(data.size() ==2) {
            if (StringUtils.isEmpty(data.get(1))) {
                String guid = data.get(0);
                List<AddrObj> list = fiasBean.listAddrObj(ru.simplgroupp.fias.persistence.Level.CITY.getID(), null, guid, Boolean.TRUE);
                return "{\"status\":\"ok\",\"answer\":\"" + getSortFIASList(list) + "\"}";
            } else {
                String guid = data.get(1);
                List<AddrObj> list = fiasBean.listAddrObj(ru.simplgroupp.fias.persistence.Level.CITY.getID(), null, guid, Boolean.TRUE);
                return "{\"status\":\"ok\",\"answer\":\"" + getSortFIASList(list) + "\"}";
            }
        }
        return "{\"status\":\"ok\",\"answer\":\"\"}";
    }

    /**
     * Возвращает из ФИАС список населенных пунктов города или района
     * @param data 0 - регион, 1 - район, 2 - город
     * @return
     */
    @POST
    @Path("/lplace")
    @Consumes("application/json")
    @Produces(MediaType.TEXT_HTML + ";charset=utf-8")
    public String getArea(ArrayList<String> data) {
        if (data.size() ==3) {
            if (!StringUtils.isEmpty(data.get(2))) {
                // выбран город
                String guid = data.get(2);
                List<AddrObj> list = fiasBean.listAddrObj(ru.simplgroupp.fias.persistence.Level.NP.getID(), null, guid, Boolean.TRUE);
                list.addAll(fiasBean.listAddrObj(ru.simplgroupp.fias.persistence.Level.CITY_TERR.getID(), null, guid, Boolean.TRUE));
                list.addAll(fiasBean.listAddrObj(ru.simplgroupp.fias.persistence.Level.DOP_DOP_TERR.getID(), null, guid, Boolean.TRUE));
                list.addAll(fiasBean.listAddrObj(ru.simplgroupp.fias.persistence.Level.DOP_TERR.getID(), null, guid, Boolean.TRUE));
                return "{\"status\":\"ok\",\"answer\":\"" + getSortFIASList(list) + "\"}";
            }else{
                if(!StringUtils.isEmpty(data.get(1))){
                    // Выбран район
                    String guid = data.get(1);
                    List<AddrObj> list = fiasBean.listAddrObj(ru.simplgroupp.fias.persistence.Level.NP.getID(), null, guid, Boolean.TRUE);
                    list.addAll(fiasBean.listAddrObj(ru.simplgroupp.fias.persistence.Level.CITY_TERR.getID(), null, guid, Boolean.TRUE));
                    list.addAll(fiasBean.listAddrObj(ru.simplgroupp.fias.persistence.Level.DOP_DOP_TERR.getID(), null, guid, Boolean.TRUE));
                    list.addAll(fiasBean.listAddrObj(ru.simplgroupp.fias.persistence.Level.DOP_TERR.getID(), null, guid, Boolean.TRUE));
                    return "{\"status\":\"ok\",\"answer\":\"" + getSortFIASList(list) + "\"}";
                }
            }
        }
        return "{\"status\":\"ok\",\"answer\":\"\"}";
    }

    /**
     * Возвращает из ФИАС список улиц города или населенного пункта
     * @param data 0 - регион, 1 - район, 2 - город, 3 - населенный пункт
     * @return
     */
    @POST
    @Path("/street")
    @Consumes("application/json")
    @Produces(MediaType.TEXT_HTML + ";charset=utf-8")
    public String getStreet(ArrayList<String> data) {
        if (data.size() ==4) {
            if (StringUtils.isNotEmpty(data.get(3))) {
                // выбран населенный пункт
                String guid = data.get(3);
                List<AddrObj> list = fiasBean.listAddrObj(ru.simplgroupp.fias.persistence.Level.STREET.getID(), null, guid, Boolean.TRUE);
                return "{\"status\":\"ok\",\"answer\":\"" + getSortFIASList(list) + "\"}";
            }else{
                if(StringUtils.isNotEmpty(data.get(2))){
                    // Выбран город
                    String guid = data.get(2);
                    List<AddrObj> list = fiasBean.listAddrObj(ru.simplgroupp.fias.persistence.Level.STREET.getID(), null, guid, Boolean.TRUE);
                    return "{\"status\":\"ok\",\"answer\":\"" + getSortFIASList(list) + "\"}";
                }else{
                    if(StringUtils.isNotEmpty(data.get(0))){
                        // выбран регион
                        String guid = data.get(0);
                        List<AddrObj> list = fiasBean.listAddrObj(ru.simplgroupp.fias.persistence.Level.STREET.getID(), null, guid, Boolean.TRUE);
                        return "{\"status\":\"ok\",\"answer\":\"" + getSortFIASList(list) + "\"}";
                    }
                }
            }
        }
        return "{\"status\":\"ok\",\"answer\":\"\"}";
    }

    /**
     * Возвращает адресные объекты для селектов
     * @param list
     * @return
     */
    private String getSortFIASList(List<AddrObj> list){
        // Отсортируем по алфавиту
        Collections.sort(list, new Comparator<AddrObj>() {
            @Override
            public int compare(AddrObj o1, AddrObj o2) {
                return o1.getFormalName().compareTo(o2.getFormalName());
            }

        });
        StringBuilder builder = new StringBuilder();
        for (Iterator<AddrObj> i = list.iterator(); i.hasNext(); ) {
            AddrObj o = i.next();
            builder.append("<i><span class='title'>" + o.getFormalName() + " " + o.getType().getName().toLowerCase() + "</span><span class='guid'>" + o.getAOGUID() + "</span></i>");
        }
        return builder.toString();
    }
}
