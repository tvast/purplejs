package io.purplejs.boot.internal.request;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

import javax.servlet.http.Part;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import io.purplejs.http.MultipartForm;
import io.purplejs.http.MultipartItem;

final class MultipartFormImpl
    implements MultipartForm
{
    private final Multimap<String, MultipartItem> map;

    MultipartFormImpl( final Iterable<Part> parts )
    {
        this.map = HashMultimap.create();
        for ( final Part part : parts )
        {
            final MultipartItemImpl item = new MultipartItemImpl( part );
            this.map.put( item.getName(), item );
        }
    }

    @Override
    public boolean isEmpty()
    {
        return this.map.isEmpty();
    }

    @Override
    public int getSize()
    {
        return this.map.size();
    }

    @Override
    public Optional<MultipartItem> get( final String name )
    {
        final Collection<MultipartItem> items = this.map.get( name );
        return items.isEmpty() ? Optional.empty() : Optional.of( items.iterator().next() );
    }

    @Override
    public void delete()
    {
        this.map.values().forEach( this::delete );
    }

    @Override
    public Iterator<MultipartItem> iterator()
    {
        return this.map.values().iterator();
    }

    private void delete( final MultipartItem item )
    {
        ( (MultipartItemImpl) item ).delete();
    }
}
