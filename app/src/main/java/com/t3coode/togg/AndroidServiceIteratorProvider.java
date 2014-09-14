package com.t3coode.togg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.util.Log;

import com.sun.jersey.spi.service.ServiceFinder.ServiceIteratorProvider;

public class AndroidServiceIteratorProvider<T> extends
        ServiceIteratorProvider<T> {

    private static final String TAG = AndroidServiceIteratorProvider.class
            .getSimpleName();
    private static final String MESSAGE = "Unable to load provider";

    private static final HashMap<String, String[]> SERVICES = new HashMap<String, String[]>();

    private static final String[] com_sun_jersey_spi_HeaderDelegateProvider = new String[] {
            "com.sun.jersey.core.impl.provider.header.LocaleProvider",
            "com.sun.jersey.core.impl.provider.header.EntityTagProvider",
            "com.sun.jersey.core.impl.provider.header.MediaTypeProvider",
            "com.sun.jersey.core.impl.provider.header.CacheControlProvider",
            "com.sun.jersey.core.impl.provider.header.NewCookieProvider",
            "com.sun.jersey.core.impl.provider.header.CookieProvider",
            "com.sun.jersey.core.impl.provider.header.URIProvider",
            "com.sun.jersey.core.impl.provider.header.DateProvider",
            "com.sun.jersey.core.impl.provider.header.StringProvider"

    };
    private static final String[] com_sun_jersey_spi_inject_InjectableProvider = new String[] {
            // "com.sun.jersey.core.impl.provider.xml.SAXParserContextProvider",
            // "com.sun.jersey.core.impl.provider.xml.XMLStreamReaderContextProvider",
            "com.sun.jersey.core.impl.provider.xml.DocumentBuilderFactoryProvider",
            "com.sun.jersey.core.impl.provider.xml.TransformerFactoryProvider",
            "com.sun.jersey.multipart.impl.MultiPartConfigProvider" };
    private static final String[] javax_ws_rs_ext_MessageBodyReader = new String[] {
            "com.sun.jersey.core.impl.provider.entity.StringProvider",
            "com.sun.jersey.core.impl.provider.entity.ByteArrayProvider",
            "com.sun.jersey.core.impl.provider.entity.FileProvider",
            "com.sun.jersey.core.impl.provider.entity.InputStreamProvider",
            // "com.sun.jersey.core.impl.provider.entity.DataSourceProvider",
            // "com.sun.jersey.core.impl.provider.entity.RenderedImageProvider",
            // "com.sun.jersey.core.impl.provider.entity.MimeMultipartProvider",
            "com.sun.jersey.core.impl.provider.entity.FormProvider",
            "com.sun.jersey.core.impl.provider.entity.FormMultivaluedMapProvider",
            // "com.sun.jersey.core.impl.provider.entity.XMLRootElementProvider$App",
            // "com.sun.jersey.core.impl.provider.entity.XMLRootElementProvider$Text",
            // "com.sun.jersey.core.impl.provider.entity.XMLRootElementProvider$General",
            // "com.sun.jersey.core.impl.provider.entity.XMLJAXBElementProvider$App",
            // "com.sun.jersey.core.impl.provider.entity.XMLJAXBElementProvider$Text",
            // "com.sun.jersey.core.impl.provider.entity.XMLJAXBElementProvider$General",
            // "com.sun.jersey.core.impl.provider.entity.XMLListElementProvider$App",
            // "com.sun.jersey.core.impl.provider.entity.XMLListElementProvider$Text",
            // "com.sun.jersey.core.impl.provider.entity.XMLListElementProvider$General",
            "com.sun.jersey.core.impl.provider.entity.ReaderProvider",
            "com.sun.jersey.core.impl.provider.entity.DocumentProvider",
            "com.sun.jersey.core.impl.provider.entity.SourceProvider$StreamSourceReader",
            // "com.sun.jersey.core.impl.provider.entity.SourceProvider$SAXSourceReader",
            "com.sun.jersey.core.impl.provider.entity.SourceProvider$DOMSourceReader",
            // "com.sun.jersey.core.impl.provider.entity.XMLRootObjectProvider$App",
            // "com.sun.jersey.core.impl.provider.entity.XMLRootObjectProvider$Text",
            // "com.sun.jersey.core.impl.provider.entity.XMLRootObjectProvider$General",
            "com.sun.jersey.core.impl.provider.entity.EntityHolderReader",
    // "com.sun.jersey.multipart.impl.MultiPartReaderClientSide",
    // "com.sun.jersey.multipart.impl.MultiPartReaderServerSide"
    };
    private static final String[] javax_ws_rs_ext_MessageBodyWriter = new String[] {
            "com.sun.jersey.core.impl.provider.entity.StringProvider",
            "com.sun.jersey.core.impl.provider.entity.ByteArrayProvider",
            "com.sun.jersey.core.impl.provider.entity.FileProvider",
            "com.sun.jersey.core.impl.provider.entity.InputStreamProvider",
            // "com.sun.jersey.core.impl.provider.entity.DataSourceProvider",
            // "com.sun.jersey.core.impl.provider.entity.RenderedImageProvider",
            // "com.sun.jersey.core.impl.provider.entity.MimeMultipartProvider",
            "com.sun.jersey.core.impl.provider.entity.FormProvider",
            "com.sun.jersey.core.impl.provider.entity.FormMultivaluedMapProvider",
            // "com.sun.jersey.core.impl.provider.entity.XMLRootElementProvider$App",
            // "com.sun.jersey.core.impl.provider.entity.XMLRootElementProvider$Text",
            // "com.sun.jersey.core.impl.provider.entity.XMLRootElementProvider$General",
            // "com.sun.jersey.core.impl.provider.entity.XMLJAXBElementProvider$App",
            // "com.sun.jersey.core.impl.provider.entity.XMLJAXBElementProvider$Text",
            // "com.sun.jersey.core.impl.provider.entity.XMLJAXBElementProvider$General",
            // "com.sun.jersey.core.impl.provider.entity.XMLListElementProvider$App",
            // "com.sun.jersey.core.impl.provider.entity.XMLListElementProvider$Text",
            // "com.sun.jersey.core.impl.provider.entity.XMLListElementProvider$General",
            "com.sun.jersey.core.impl.provider.entity.ReaderProvider",
            "com.sun.jersey.core.impl.provider.entity.DocumentProvider",
            "com.sun.jersey.core.impl.provider.entity.StreamingOutputProvider",
            // "com.sun.jersey.core.impl.provider.entity.SourceProvider$SourceWriter",
            "com.sun.jersey.multipart.impl.MultiPartWriter" };

    private static final String[] com_sun_jersey_spi_container_ResourceMethodCustomInvokerDispatchProvider = { "com.sun.jersey.multipart.impl.FormDataMultiPartDispatchProvider" };

    static {
        SERVICES.put("com.sun.jersey.spi.HeaderDelegateProvider",
                com_sun_jersey_spi_HeaderDelegateProvider);
        SERVICES.put("com.sun.jersey.spi.inject.InjectableProvider",
                com_sun_jersey_spi_inject_InjectableProvider);
        SERVICES.put("javax.ws.rs.ext.MessageBodyReader",
                javax_ws_rs_ext_MessageBodyReader);
        SERVICES.put("javax.ws.rs.ext.MessageBodyWriter",
                javax_ws_rs_ext_MessageBodyWriter);
        SERVICES.put(
                "com.sun.jersey.spi.container.ResourceMethodCustomInvokerDispatchProvider",
                com_sun_jersey_spi_container_ResourceMethodCustomInvokerDispatchProvider);

        SERVICES.put("jersey-client-components", new String[] {});
        SERVICES.put("com.sun.jersey.client.proxy.ViewProxyProvider",
                new String[] {});

        SERVICES.put(
                "com.sun.jersey.server.impl.model.method.dispatch.ResourceMethodDispatchProvider",
                new String[] { "com.sun.jersey.multipart.impl.FormDataMultiPartDispatchProvider" });
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<Class<T>> createClassIterator(Class<T> service,
            String serviceName, ClassLoader loader,
            boolean ignoreOnClassNotFound) {

        String[] classesNames = SERVICES.get(serviceName);
        int length = classesNames.length;
        ArrayList<Class<T>> classes = new ArrayList<Class<T>>(length);
        for (int i = 0; i < length; i++) {
            try {
                classes.add((Class<T>) Class.forName(classesNames[i]));
            } catch (ClassNotFoundException e) {
                Log.v(TAG, MESSAGE, e);
            }
        }
        return classes.iterator();
    }

    @Override
    public Iterator<T> createIterator(Class<T> service, String serviceName,
            ClassLoader loader, boolean ignoreOnClassNotFound) {

        String[] classesNames = SERVICES.get(serviceName);
        int length = classesNames.length;
        ArrayList<T> classes = new ArrayList<T>(length);
        for (int i = 0; i < length; i++) {
            try {
                classes.add(service.cast(Class.forName(classesNames[i])
                        .newInstance()));
            } catch (IllegalAccessException e) {
                Log.v(TAG, MESSAGE, e);
            } catch (InstantiationException e) {
                Log.v(TAG, MESSAGE, e);
            } catch (ClassNotFoundException e) {
                Log.v(TAG, MESSAGE, e);
            }
        }

        return classes.iterator();
    }
}