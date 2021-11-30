package test;

import org.junit.Assert;
import org.junit.Test;
import ru.lanit.dibr.utils.gui.PopupBlock;
import ru.lanit.dibr.utils.utils.XmlUtils;

/**
 * User: Vova
 * Date: 12.12.12
 * Time: 0:15
 */
public class TestXmlUtils {

    @Test
    public void test_FormatXml_spaces() {
        String block = "[12/11/12 19:37:27:740 MSK] 0000001f SystemOut     O 2012-12-11 19:37:27,739 [stenerThreadPool : 0] [  STANDARD] [SberbankSvc:01.09.01] (quest.Sberbank_Int_ESB_.Action) INFO  JMS|Sberbank|Services|Invoke|A65BEB39EEF8B294961E5F7959E2D18F9  - Outgoing message to ESB: <ns1:Envelope xmlns:ns1=\"http://schemas.xmlsoap.org/soap/envelope/\"> <ns3:Header xmlns:ns1=\"http://www.ibm.com/KD4Soap\" xmlns:ns2=\"http://sbrf.ru/prpc/mq/headers\" xmlns:ns3=\"http://schemas.xmlsoap.org/soap/envelope/\">  <ns2:AsyncHeader> <ns2:message-id>300C0D3E4AAB48E18060A1D36BB2F1B0</ns2:message-id> <ns2:request-time>2012-12-11T19:37:26.323Z</ns2:request-time>  <ns2:eis-name>urn:sbrfsystems:99-iask</ns2:eis-name> <ns2:system-id>urn:sbrfsystems:99-bbmo</ns2:system-id> <ns2:operation-name>SrvRegisterLegalPersonApplication</ns2:operation-name> <ns2:operation-version>5</ns2:operation-version> <ns2:user-id>Shevtsova_OL</ns2:user-id> <ns2:user-name>ШЕВЦОВА Ольга Леонидовна</ns2:user-name> </ns2:AsyncHeader> </ns3:Header>  <ns3:Body xmlns:ns1=\"http://sbrf.ru/prpc/bbmo/10\" xmlns:ns2=\"http://sbrf.ru/prpc/bbmo/commonTypes/10\" xmlns:ns3=\"http://schemas.xmlsoap.org/soap/envelope/\">  <ns1:SrvRegisterLegalPersonApplicationRs> <ns2:ResultCode>3</ns2:ResultCode> <ns2:ErrorDetails> <ns2:ErrorMessage>Ошибка! В системе существует открытая задача по обработке данной кредитной сделки. Создание новой задачи возможно только после завершение обработки существующей!</ns2:ErrorMessage>  </ns2:ErrorDetails>  </ns1:SrvRegisterLegalPersonApplicationRs>  </ns3:Body>  </ns1:Envelope>";
        Assert.assertEquals("[12/11/12 19:37:27:740 MSK] 0000001f SystemOut     O 2012-12-11 19:37:27,739 [stenerThreadPool : 0] [  STANDARD] [SberbankSvc:01.09.01] (quest.Sberbank_Int_ESB_.Action) INFO  JMS|Sberbank|Services|Invoke|A65BEB39EEF8B294961E5F7959E2D18F9  - Outgoing message to ESB: \n" +
                "<ns1:Envelope xmlns:ns1=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "  <ns3:Header xmlns:ns1=\"http://www.ibm.com/KD4Soap\" xmlns:ns2=\"http://sbrf.ru/prpc/mq/headers\" xmlns:ns3=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "    <ns2:AsyncHeader>\n" +
                "      <ns2:message-id>300C0D3E4AAB48E18060A1D36BB2F1B0</ns2:message-id>\n" +
                "      <ns2:request-time>2012-12-11T19:37:26.323Z</ns2:request-time>\n" +
                "      <ns2:eis-name>urn:sbrfsystems:99-iask</ns2:eis-name>\n" +
                "      <ns2:system-id>urn:sbrfsystems:99-bbmo</ns2:system-id>\n" +
                "      <ns2:operation-name>SrvRegisterLegalPersonApplication</ns2:operation-name>\n" +
                "      <ns2:operation-version>5</ns2:operation-version>\n" +
                "      <ns2:user-id>Shevtsova_OL</ns2:user-id>\n" +
                "      <ns2:user-name>ШЕВЦОВА Ольга Леонидовна</ns2:user-name>\n" +
                "    </ns2:AsyncHeader>\n" +
                "  </ns3:Header>\n" +
                "  <ns3:Body xmlns:ns1=\"http://sbrf.ru/prpc/bbmo/10\" xmlns:ns2=\"http://sbrf.ru/prpc/bbmo/commonTypes/10\" xmlns:ns3=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "    <ns1:SrvRegisterLegalPersonApplicationRs>\n" +
                "      <ns2:ResultCode>3</ns2:ResultCode>\n" +
                "      <ns2:ErrorDetails>\n" +
                "        <ns2:ErrorMessage>Ошибка! В системе существует открытая задача по обработке данной кредитной сделки. Создание новой задачи возможно только после завершение обработки существующей!</ns2:ErrorMessage>\n" +
                "      </ns2:ErrorDetails>\n" +
                "    </ns1:SrvRegisterLegalPersonApplicationRs>\n" +
                "  </ns3:Body>\n" +
                "</ns1:Envelope>\n", XmlUtils.formatXml(block));
    }

    @Test
    public void test_FormatXml_longLines() {
        String block = "<pkg><pkg_name>_DIC5_00000_06_CC19M5_11122012_193314_03_000_F-28854</pkg_name><pkg_tot_docs>5</pkg_tot_docs><pkg_tot_pages>3</pkg_tot_pages><doc><doc_name>FEFF04170430044F0432043B0435043D043804350020043E002004370430043A0440044B044204380438002F043F0435044004350432043E043404350020041F0421</doc_name><doc_tot_pages>1</doc_tot_pages><doc_ext_uid>0000275B</doc_ext_uid><page><page_num>FEFF04170430043A0440044B0442043804350020041F0421005F043F0435044004350432043E0434005F041F0421005F0032002E007000640066</page_num><page_ext_uid>01000001</page_ext_uid></page></doc><doc><doc_name>FEFF041F04400438043B043E04360435043D043804350020043A002004370430044F0432043B0435043D0438044E</doc_name><doc_tot_pages>0</doc_tot_pages><doc_ext_uid>0000275C</doc_ext_uid></doc><doc><doc_name>FEFF00410042004200590059</doc_name><doc_tot_pages>1</doc_tot_pages><doc_ext_uid>00000000</doc_ext_uid><page><page_num>FEFF0421043B0443043604350431043D0430044F00200438043D0444043E0440043C043004460438044F002C0020044104420440002E00200031</page_num><page_ext_uid>00AAAAAA</page_ext_uid></page></doc><doc><doc_name>FEFF041204350434043E043C043E04410442044C002004310430043D043A043E04320441043A043E0433043E0020043A043E043D04420440043E043B044F002004380020043F04300441043F043E044004420020044104340435043B043A04380020044100200441043E043E043104490435043D04380435043C0020044100200438043D0441044204400443043A04460438043504390020043A00200434043504390441044204320438044F043C00200434043B044F002004120421041F</doc_name><doc_tot_pages>1</doc_tot_pages><doc_ext_uid>00002770</doc_ext_uid><page><page_num>FEFF041E0444043E0440043C043B0435043D043804350020041F04210020043A0440043504340438044200200031002E007000640066</page_num><page_ext_uid>01000003</page_ext_uid></page></doc><doc><doc_name>FEFF041E0442043204350442002C0020043F043E0434043F043804410430043D043D044B0439002004310430043D043A043E043C</doc_name><doc_tot_pages>0</doc_tot_pages><doc_ext_uid>0000278C</doc_ext_uid></doc><ext_change_log><AddPage>00002770-01000003</AddPage></ext_change_log></pkg>";
        System.out.println(XmlUtils.formatXml(block));
        Assert.assertEquals("<pkg>\n" +
                "  <pkg_name>_DIC5_00000_06_CC19M5_11122012_193314_03_000_F-28854</pkg_name>\n" +
                "  <pkg_tot_docs>5</pkg_tot_docs>\n" +
                "  <pkg_tot_pages>3</pkg_tot_pages>\n" +
                "  <doc>\n" +
                "    <doc_name>FEFF04170430044F0432043B0435043D043804350020043E002004370430043A0440044B044204380438002F043F0435044004350432043E043404350020041F0421</doc_name>\n" +
                "    <doc_tot_pages>1</doc_tot_pages>\n" +
                "    <doc_ext_uid>0000275B</doc_ext_uid>\n" +
                "    <page>\n" +
                "      <page_num>FEFF04170430043A0440044B0442043804350020041F0421005F043F0435044004350432043E0434005F041F0421005F0032002E007000640066</page_num>\n" +
                "      <page_ext_uid>01000001</page_ext_uid>\n" +
                "    </page>\n" +
                "  </doc>\n" +
                "  <doc>\n" +
                "    <doc_name>FEFF041F04400438043B043E04360435043D043804350020043A002004370430044F0432043B0435043D0438044E</doc_name>\n" +
                "    <doc_tot_pages>0</doc_tot_pages>\n" +
                "    <doc_ext_uid>0000275C</doc_ext_uid>\n" +
                "  </doc>\n" +
                "  <doc>\n" +
                "    <doc_name>FEFF00410042004200590059</doc_name>\n" +
                "    <doc_tot_pages>1</doc_tot_pages>\n" +
                "    <doc_ext_uid>00000000</doc_ext_uid>\n" +
                "    <page>\n" +
                "      <page_num>FEFF0421043B0443043604350431043D0430044F00200438043D0444043E0440043C043004460438044F002C0020044104420440002E00200031</page_num>\n" +
                "      <page_ext_uid>00AAAAAA</page_ext_uid>\n" +
                "    </page>\n" +
                "  </doc>\n" +
                "  <doc>\n" +
                "    <doc_name>FEFF041204350434043E043C043E04410442044C002004310430043D043A043E04320441043A043E0433043E0020043A043E043D04420440043E043B044F002004380020043F04300441043F043E044004420020044104340435043B043A04380020044100200441043E043E043104490435043D04380435043C0020044100200438043D0441044204400443043A04460438043504390020043A00200434043504390441044204320438044F043C00200434043B044F002004120421041F</doc_name>\n" +
                "    <doc_tot_pages>1</doc_tot_pages>\n" +
                "    <doc_ext_uid>00002770</doc_ext_uid>\n" +
                "    <page>\n" +
                "      <page_num>FEFF041E0444043E0440043C043B0435043D043804350020041F04210020043A0440043504340438044200200031002E007000640066</page_num>\n" +
                "      <page_ext_uid>01000003</page_ext_uid>\n" +
                "    </page>\n" +
                "  </doc>\n" +
                "  <doc>\n" +
                "    <doc_name>FEFF041E0442043204350442002C0020043F043E0434043F043804410430043D043D044B0439002004310430043D043A043E043C</doc_name>\n" +
                "    <doc_tot_pages>0</doc_tot_pages>\n" +
                "    <doc_ext_uid>0000278C</doc_ext_uid>\n" +
                "  </doc>\n" +
                "  <ext_change_log>\n" +
                "    <AddPage>00002770-01000003</AddPage>\n" +
                "  </ext_change_log>\n" +
                "</pkg>\n", XmlUtils.formatXml(block));
    }
}
