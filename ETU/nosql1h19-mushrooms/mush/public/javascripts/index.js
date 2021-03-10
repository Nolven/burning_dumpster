
w3_close_left();
$('#queryf').submit(function() {
    form = $(this);
    data = collectData();
    for( k in data)
    {
        if( k !== 'name' )
            form.append('<input type="hidden" name="'+k+'" value='+data[k]+'>');
    }
    return true;
});

