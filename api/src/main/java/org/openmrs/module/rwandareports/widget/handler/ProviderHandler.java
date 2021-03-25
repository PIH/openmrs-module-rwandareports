package org.openmrs.module.rwandareports.widget.handler;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Provider;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlwidgets.web.WidgetConfig;
import org.openmrs.module.htmlwidgets.web.handler.CodedHandler;
import org.openmrs.module.htmlwidgets.web.html.CodedWidget;
import org.openmrs.module.htmlwidgets.web.html.Option;


import java.util.List;

@Handler(supports={Provider.class}, order=1)
public class ProviderHandler extends CodedHandler {

    /**
     * @see CodedHandler#populateOptions(WidgetConfig, CodedWidget)
     */
    @Override
    public void populateOptions(WidgetConfig config, CodedWidget widget) {

        List<Provider> providers = Context.getProviderService().getProviders(null,null,null,null,false);

        for(Provider p:providers){
            widget.addOption(new Option(p.getUuid(),p.getName(),null,null),config);


        }
    }

    /**
     * @see WidgetHandler#parse(String, Class<?>)
     */
    @Override
    public Object parse(String input, Class<?> type) {
        if(StringUtils.isNotBlank(input)){
            Provider provider = Context.getProviderService().getProviderByUuid(input);
            return provider;
        }

        return null;
    }
}
