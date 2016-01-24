package org.vaadin.addon.leaflet.draw.client;

import org.peimari.gleaflet.client.*;
import org.peimari.gleaflet.client.resources.LeafletDrawResourceInjector;
import org.vaadin.addon.leaflet.client.*;
import org.vaadin.addon.leaflet.draw.LEditing;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.shared.ui.Connect;

@Connect(LEditing.class)
public class LeafletDrawEditingConnector extends AbstractExtensionConnector {
	static {
		LeafletDrawResourceInjector.ensureInjected();
	}

	private LeafletDrawEditingServerRcp rpc = RpcProxy.create(
			LeafletDrawEditingServerRcp.class, this);

	private EditableFeature ef;

	@Override
	protected void extend(final ServerConnector target) {
	}

	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				final AbstractLeafletLayerConnector c = (AbstractLeafletLayerConnector) getParent();
				ef = (EditableFeature) c.getLayer();

				ef.addEditListener(new FeatureEditedListener() {

					@Override
					public void onEdit() {

						if (c instanceof LeafletCircleConnector) {
							Circle circle = (Circle) c.getLayer();
							rpc.circleModified(c, U.toPoint(circle.getLatLng()), circle.getRadius());
						} else if (c instanceof LeafletPolygonConnector) {
						   	Polygon p = (Polygon) c.getLayer();
						   	rpc.polygonModified(c,
						   	      		U.toPointArray(p.getLatLngs()));
						} else {
							// Polyline
							Polyline p = (Polyline) c.getLayer();
							rpc.polylineModified(c,
									U.toPointArray(p.getLatLngs()));
						}

					}
				});
				ef.enable();
			}
		});
	}
	
	@Override
	public void onUnregister() {
		super.onUnregister();
		ef.disable();
	}

}
