package io.purplejs.http.internal.handler;

import java.util.Collection;
import java.util.Map;

import com.google.common.net.HttpHeaders;

import io.purplejs.core.json.JsonGenerator;
import io.purplejs.core.json.JsonSerializable;
import io.purplejs.http.Headers;
import io.purplejs.http.Parameters;
import io.purplejs.http.Request;

final class JsonRequest
    implements JsonSerializable
{
    private final Request request;

    JsonRequest( final Request request )
    {
        this.request = request;
    }

    @Override
    public void serialize( final JsonGenerator gen )
    {
        gen.map();
        gen.value( "method", this.request.getMethod() );

        gen.value( "scheme", this.request.getUri().getScheme() );
        gen.value( "host", this.request.getUri().getHost() );
        gen.value( "port", this.request.getUri().getPort() );
        gen.value( "path", this.request.getUri().getPath() );
        gen.value( "uri", this.request.getUri() );
        gen.value( "contentType", this.request.getContentType() );
        gen.value( "contentLength", this.request.getContentLength() );
        gen.value( "webSocket", this.request.isWebSocket() );
        gen.value( "wrapped", this.request );

        //gen.value( "remoteAddress", this.request.getRemoteAddress() );

        serializeParameters( gen, this.request.getParameters() );
        serializeHeaders( gen, this.request.getHeaders() );
        serializeCookies( gen, this.request.getCookies() );

        gen.end();
    }

    private void serializeParameters( final JsonGenerator gen, final Parameters params )
    {
        gen.map( "params" );
        for ( final Map.Entry<String, Collection<String>> entry : params.asMap().entrySet() )
        {
            final Collection<String> values = entry.getValue();
            if ( values.size() == 1 )
            {
                gen.value( entry.getKey(), values.iterator().next() );
            }
            else
            {
                gen.array( entry.getKey() );
                values.forEach( gen::value );
                gen.end();
            }
        }
        gen.end();
    }

    private boolean shouldSerializeHeader( final String name )
    {
        return !name.equalsIgnoreCase( HttpHeaders.COOKIE );
    }

    private void serializeHeaders( final JsonGenerator gen, final Headers headers )
    {
        gen.map( "headers" );
        headers.entrySet().stream().filter( entry -> shouldSerializeHeader( entry.getKey() ) ).forEach(
            entry -> gen.value( entry.getKey(), entry.getValue() ) );
        gen.end();
    }

    private void serializeCookies( final JsonGenerator gen, final Map<String, String> cookies )
    {
        gen.map( "cookies" );
        cookies.forEach( gen::value );
        gen.end();
    }
}
