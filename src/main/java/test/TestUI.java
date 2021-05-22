package test;

import ru.lanit.dibr.utils.core.TestStringSource;
import org.junit.Test;
import ru.lanit.dibr.utils.core.LogSource;
import ru.lanit.dibr.utils.gui.HotKeysInfo;
import ru.lanit.dibr.utils.gui.LogFrame;
import ru.lanit.dibr.utils.gui.PopupBlock;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: Vladimir
 * Date: 22.01.14
 * Time: 16:55
 * To change this template use File | Settings | File Templates.
 */
public class TestUI {

    public static void showBlockPopup() {
        try {
//            new PopupBlock("123", "2014-01-22 17:24:23,482 [j2ee14_ws,maxpri=10]] [  STANDARD] [ LoanFWPhase21:01.01] (OVEREDTASKS.Work_Cover_.Action) ERROR  maksim.nenakhov@btc.info - The work object ALFABANK-FW-LOAN-WORK ALFA-2903 could not be locked: Cannot obtain a lock on instance ALFABANK-FW-LOAN-WORK ALFA-2903, as Requestor H6C4B9233EF5F7D6E4C5C9E2915F8DC61 already has the lock", true);
            new PopupBlock("123", "asd 58111158 awsd", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showBlockPopupTestUnescapeXml() {
        try {
            new PopupBlock("123", "<a><b><c>text</c><f>&lt;d attr=&quot;qwe&quot;&gt;&lt;e&gt;piu&lt;/e&gt;&lt;/d&gt;</f></b></a>", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        showBlockPopup();
        showBlockPopupTestUnescapeXml();
//        debugDoubleBlock();
//        showHelp();

    }


    public static void debugDoubleBlock() {
        LogSource source = new TestStringSource("2014-01-22 23:48:38,282 [   WebContainer : 10] [          ] [                    ] (     services.soap.SOAPService) DEBUG localhost.localdomain|89.249.28.228  - Retrieved SOAP Action value from request URL: urn:PegaRULES:SOAP:CCM:WSCCMRM\n" +
                "2014-01-22 23:48:38,282 [   WebContainer : 10] [          ] [                    ] (     services.soap.SOAPService) INFO  localhost.localdomain|89.249.28.228  - SOAP Request Envelope:\n" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><soap:Body><ns2:WSCCMVerifyApplicationResultRequest xmlns:ns2=\"http://WSCCMRM10.ws.alfabank.ru\"><REQUEST_ID>FLECM010S14012200013</REQUEST_ID><APPLICATION_ID>M010S14012200013</APPLICATION_ID><EXTERNAL_SYSTEM_CODE>FLEC</EXTERNAL_SYSTEM_CODE><CCM_CHECK><CCM_CHECK_ID>65</CCM_CHECK_ID><CCM_CHECK_RESULT>08</CCM_CHECK_RESULT></CCM_CHECK></ns2:WSCCMVerifyApplicationResultRequest></soap:Body></soap:Envelope>\n" +
                "2014-01-22 23:48:38,311 [   WebContainer : 10] [  STANDARD] [                    ] (     services.soap.SOAPService) ERROR localhost.localdomain|89.249.28.228  - Failed to retrieve Rule-Service-SOAP instance CCM.WSCCMRM.WSCCMVerifyApplicationResultRequest using access group LoanFW:Administrators\n" +
                "com.pega.pegarules.pub.PRException: Failed to retrieve Rule-Service-SOAP instance CCM.WSCCMRM.WSCCMVerifyApplicationResultRequest using access group LoanFW:Administrators\n" +
                "From: (A314D5F3C24ED8FD837CD8B7DDCFF50B7:CCM) \n" +
                "\tat com.pega.pegarules.integration.engine.internal.services.ServiceAPI.getServiceInstance(ServiceAPI.java:2956)\n" +
                "\tat com.pega.pegarules.integration.engine.internal.services.ServiceAPI.getServiceMethodInner(ServiceAPI.java:2658)\n" +
                "\tat sun.reflect.GeneratedMethodAccessor133.invoke(Unknown Source)\n" +
                "\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n" +
                "\tat java.lang.reflect.Method.invoke(Method.java:600)\n" +
                "\tat com.pega.pegarules.session.internal.PRSessionProviderImpl.doWithRequestorLocked(PRSessionProviderImpl.java:1043)\n" +
                "\tat com.pega.pegarules.session.internal.PRSessionProviderImpl.doWithRequestorLocked(PRSessionProviderImpl.java:765)\n" +
                "\tat com.pega.pegarules.integration.engine.internal.services.ServiceAPI.getServiceMethod(ServiceAPI.java:2621)\n" +
                "\tat com.pega.pegarules.integration.engine.internal.util.SOAPUtils.parseRequestData(SOAPUtils.java:436)\n" +
                "\tat com.pega.pegarules.integration.engine.internal.services.soap.SOAPService.invoke(SOAPService.java:387)\n" +
                "\tat com.pega.pegarules.session.internal.engineinterface.etier.impl.EngineImpl._invokeEngine_privact(EngineImpl.java:312)\n" +
                "\tat com.pega.pegarules.session.internal.engineinterface.etier.impl.EngineImpl.invokeEngine(EngineImpl.java:263)\n" +
                "\tat com.pega.pegarules.session.internal.engineinterface.etier.ejb.EngineBean.invokeEngine(EngineBean.java:221)\n" +
                "\tat sun.reflect.GeneratedMethodAccessor51.invoke(Unknown Source)\n" +
                "\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n" +
                "\tat java.lang.reflect.Method.invoke(Method.java:600)\n" +
                "\tat com.pega.pegarules.internal.bootstrap.PRBootstrap.invokeMethod(PRBootstrap.java:349)\n" +
                "\tat com.pega.pegarules.internal.bootstrap.PRBootstrap.invokeMethodPropagatingThrowable(PRBootstrap.java:390)\n" +
                "\tat com.pega.pegarules.internal.bootstrap.PRBootstrap.invokeMethodPropagatingException(PRBootstrap.java:412)\n" +
                "\tat com.pega.pegarules.internal.etier.ejb.EngineBeanBoot.invokeEngine(EngineBeanBoot.java:168)\n" +
                "\tat com.pega.pegarules.internal.etier.interfaces.EJSLocalStatelessEngineBMT_f2439d86.invokeEngine(Unknown Source)\n" +
                "\tat com.pega.pegarules.priv.context.JNDIEnvironment.invokeEngineInner(JNDIEnvironment.java:277)\n" +
                "\tat com.pega.pegarules.priv.context.JNDIEnvironment.invokeEngine(JNDIEnvironment.java:222)\n" +
                "\tat com.pega.pegarules.web.impl.WebStandardImpl.makeEtierRequest(WebStandardImpl.java:383)\n" +
                "\tat com.pega.pegarules.web.impl.WebStandardImpl.doPost(WebStandardImpl.java:271)\n" +
                "\tat sun.reflect.GeneratedMethodAccessor63.invoke(Unknown Source)\n" +
                "\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n" +
                "\tat java.lang.reflect.Method.invoke(Method.java:600)\n" +
                "\tat com.pega.pegarules.internal.bootstrap.PRBootstrap.invokeMethod(PRBootstrap.java:349)\n" +
                "\tat com.pega.pegarules.internal.bootstrap.PRBootstrap.invokeMethodPropagatingThrowable(PRBootstrap.java:390)\n" +
                "\tat com.pega.pegarules.internal.bootstrap.PRBootstrap.invokeMethod(PRBootstrap.java:439)\n" +
                "\tat com.pega.pegarules.internal.web.servlet.WebStandardBoot.doPost(WebStandardBoot.java:118)\n" +
                "\tat javax.servlet.http.HttpServlet.service(HttpServlet.java:738)\n" +
                "\tat javax.servlet.http.HttpServlet.service(HttpServlet.java:831)\n" +
                "\tat com.ibm.ws.webcontainer.servlet.ServletWrapper.service(ServletWrapper.java:1661)\n" +
                "\tat com.ibm.ws.webcontainer.servlet.ServletWrapper.handleRequest(ServletWrapper.java:944)\n" +
                "\tat com.ibm.ws.webcontainer.servlet.ServletWrapper.handleRequest(ServletWrapper.java:507)\n" +
                "\tat com.ibm.ws.webcontainer.servlet.ServletWrapperImpl.handleRequest(ServletWrapperImpl.java:181)\n" +
                "\tat com.ibm.ws.webcontainer.webapp.WebApp.handleRequest(WebApp.java:3954)\n" +
                "\tat com.ibm.ws.webcontainer.webapp.WebGroup.handleRequest(WebGroup.java:276)\n" +
                "\tat com.ibm.ws.webcontainer.WebContainer.handleRequest(WebContainer.java:945)\n" +
                "\tat com.ibm.ws.webcontainer.WSWebContainer.handleRequest(WSWebContainer.java:1592)\n" +
                "\tat com.ibm.ws.webcontainer.channel.WCChannelLink.ready(WCChannelLink.java:191)\n" +
                "\tat com.ibm.ws.http.channel.inbound.impl.HttpInboundLink.handleDiscrimination(HttpInboundLink.java:453)\n" +
                "\tat com.ibm.ws.http.channel.inbound.impl.HttpInboundLink.handleNewRequest(HttpInboundLink.java:515)\n" +
                "\tat com.ibm.ws.http.channel.inbound.impl.HttpInboundLink.processRequest(HttpInboundLink.java:306)\n" +
                "\tat com.ibm.ws.http.channel.inbound.impl.HttpInboundLink.ready(HttpInboundLink.java:277)\n" +
                "\tat com.ibm.ws.tcp.channel.impl.NewConnectionInitialReadCallback.sendToDiscriminators(NewConnectionInitialReadCallback.java:214)\n" +
                "\tat com.ibm.ws.tcp.channel.impl.NewConnectionInitialReadCallback.complete(NewConnectionInitialReadCallback.java:113)\n" +
                "\tat com.ibm.ws.tcp.channel.impl.AioReadCompletionListener.futureCompleted(AioReadCompletionListener.java:175)\n" +
                "\tat com.ibm.io.async.AbstractAsyncFuture.invokeCallback(AbstractAsyncFuture.java:217)\n" +
                "\tat com.ibm.io.async.AsyncChannelFuture$1.run(AsyncChannelFuture.java:205)\n" +
                "\tat com.ibm.ws.util.ThreadPool$Worker.run(ThreadPool.java:1656)\n" +
                "2014-01-22 23:48:38,311 [   WebContainer : 10] [  STANDARD] [                    ] (     services.soap.SOAPService) INFO  localhost.localdomain|89.249.28.228  - SOAP Response Envelope:\n" +
                "<?xml version=\"1.0\"?>\n" +
                "<soap:Envelope\n" +
                "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                "xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\"\n" +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n" +
                "<soap:Body>\n" +
                "<soap:Fault>\n" +
                "<faultcode>soap:Server</faultcode>\n" +
                "<faultstring>Failed to retrieve Rule-Service-SOAP instance CCM.WSCCMRM.WSCCMVerifyApplicationResultRequest using access group LoanFW:Administrators</faultstring>\n" +
                "</soap:Fault>\n" +
                "</soap:Body>\n" +
                "</soap:Envelope>\n" +
                "\n" +
                "2014-01-22 23:50:28,365 [j2ee14_ws,maxpri=10]] [  STANDARD] [ LoanFWPhase21:01.01] (n_Work_CreditCard_Front.Action) INFO    - [ START UpdateFromParallel ]: WO ID=PIL-1084, parallelID=ABBYY, Status=Resolved, Transform=\n" +
                "2014-01-22 23:50:28,366 [j2ee14_ws,maxpri=10]] [  STANDARD] [ LoanFWPhase21:01.01] (n_Work_CreditCard_Front.Action) INFO    - Step 1: After GetChildrenToWait: ABBYY\n" +
                "2014-01-22 23:50:28,366 [j2ee14_ws,maxpri=10]] [  STANDARD] [ LoanFWPhase21:01.01] (W_Loan_Work_Application.Action) INFO    - [ START LoadParallelChildList ]: ChildIDs=ABBYY\n\n", 5, false);
        LogFrame logFrame = new LogFrame(null, null, "tst", source, "\\d{4}-\\d\\d?-\\d\\d? \\d\\d?:\\d\\d?:\\d\\d?[,:]\\d{1,3}");
        logFrame.setSize(1400, 1100);
        logFrame.setVisible(true);
        logFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void showHelp() {
        new HotKeysInfo();
    }

}
