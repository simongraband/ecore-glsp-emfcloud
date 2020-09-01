package org.eclipse.emfcloud.ecore.glsp.handler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.eclipse.emfcloud.GenericValidation;
import org.eclipse.emfcloud.ecore.glsp.model.EcoreModelServerAccess;
import org.eclipse.emfcloud.ecore.glsp.model.EcoreModelState;
import org.eclipse.emfcloud.modelserver.client.ModelServerClient;
import org.eclipse.glsp.api.action.Action;
import org.eclipse.glsp.api.action.kind.RequestMarkersAction;
import org.eclipse.glsp.api.action.kind.SetMarkersAction;
import org.eclipse.glsp.api.markers.Marker;
import org.eclipse.glsp.api.model.GraphicalModelState;
import org.eclipse.glsp.api.utils.ClientOptions;
import org.eclipse.glsp.server.actionhandler.RequestMarkersHandler;

public class EcoreRequestMarkersActionHandler extends RequestMarkersHandler {

    @Override
    public List<Action> executeAction(final RequestMarkersAction action, final GraphicalModelState modelState) {
        GenericValidation validation;
        String modeluri = modelState.getClientOptions().get(ClientOptions.SOURCE_URI);
        List<Marker> markers = new ArrayList<>();
        try {
            ModelServerClient modelServerClient = new ModelServerClient("http://localhost:8081/api/v1/");
            validation = new GenericValidation(modelServerClient);
            EcoreModelState ecoreModelState = EcoreModelState.getModelState(modelState);
            EcoreModelServerAccess access = new EcoreModelServerAccess(modeluri, modelServerClient, ecoreModelState.getIndex());
            access.setEcoreFacade(EcoreModelState.getEcoreFacade(modelState));
            access.update();
            Thread.sleep(1000);
            validation.validate(modeluri);
            Thread.sleep(1000);
            markers = validation.getMarkers(modeluri);
            markers = copyMarkers(markers, ecoreModelState);
            System.out.println("Test");
            
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
       return listOf(new SetMarkersAction(markers));
   }

   public List<Marker> copyMarkers(List<Marker> markers, EcoreModelState ecoreModelState){
        List<Marker> newMarkers = new ArrayList();
        for(Marker m: markers){
            newMarkers.add(new Marker(m.getLabel(), m.getDescription(), ecoreModelState.getRoot().getId(), m.getType()));
        }
        return newMarkers;
   }
}