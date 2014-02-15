
<link href="<@ofbizContentUrl>/images/jquery/plugins/steps/jquery.steps.css</@ofbizContentUrl>" rel="stylesheet">
<script type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/steps/jquery.steps.js</@ofbizContentUrl>"></script>


<script type="text/javascript">

    $(document).ready(function(){
            $("#wizard-2").steps({
                headerTag: "h3",
                bodyTag: "section",
                transitionEffect: "slideLeft",
                onStepChanging: function (event, currentIndex, newIndex)
                {
                    return true;
                },
                onFinishing: function (event, currentIndex)
                {
                    return true;
                },
                onFinished: function (event, currentIndex)
                {
                    alert("Submitted!");
                }
            });
    	
	}); 
</script>

                <form id="form-2" action="#">
                    <div id="wizard-2">
                        <h3>Product Returns</h3>
                        <section>
                                <li>Foo1</li>
                                <li>Bar1</li>
                                <li>Foobar1</li>
                        </section>

                        <h3>Crate Returns</h3>
                        <section>
                                <li>Foo2</li>
                                <li>Bar2</li>
                                <li>Foobar2</li>
                        </section>

                        <h3>Vehicle staus change</h3>
                        <section>
                            <ul>
                                <li>Foo3</li>
                                <li>Bar3</li>
                                <li>Foobar3</li>
                            </ul>
                        </section>

                        <h3>Finalize</h3>
                        <section>
                                <li>Foo4</li>
                                <li>Bar4</li>
                                <li>Foobar4</li>                        
                        </section>
                    </div>
                </form>

 