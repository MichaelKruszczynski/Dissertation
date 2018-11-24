$( document ).ready(function() {
	var canvas = document.getElementById('paint');
	var ctx = canvas.getContext('2d');
	 
	var sketch = document.getElementById('sketch');
	var sketch_style = getComputedStyle(sketch);
	var divider= 2; // 4 works
	var divider2= 4;// 4 works
	canvas.width = 500/divider;
	canvas.height = 250/divider2;
	
	var mouse = {x: 0, y: 0};
	 
	 	var asd =$('#signatureString');
	 	console.log(asd);
	 	console.log(asd.val());
	 	if(asd.val().length === 0){
	 		console.log('cannot fill the canvas: no data');
	 	} else {
	 		signatureLoad(asd.val());
	 	}
	 	
	 
	/* Mouse Capturing Work */
	canvas.addEventListener('mousemove', function(e) {
	  mouse.x = e.pageX - this.offsetLeft;
	  mouse.y = e.pageY - this.offsetTop;
	}, false);
	
	/* Drawing on Paint App */
	ctx.lineJoin = 'round';
	ctx.lineCap = 'round';
	
	ctx.strokeStyle = "black";
	
	//ctx.strokeStyle = 
	//ctx.strokeStyle = document.settings.colour[1].value;
	 
	canvas.addEventListener('mousedown', function(e) {
	    ctx.beginPath();
	    ctx.moveTo(mouse.x, mouse.y);
	 
	    canvas.addEventListener('mousemove', onPaint, false);
	}, false);
	 
	canvas.addEventListener('mouseup', function() {
	    canvas.removeEventListener('mousemove', onPaint, false);
	}, false);
	 
	var onPaint = function() {
	    ctx.lineTo(mouse.x, mouse.y);
	    ctx.stroke();
	};
	
	            
    $("#signatureButton").on("click",  function () {
    	signatureButtonSave();
    });
	
	function signatureButtonSave() {
		var imgData= ctx.getImageData(0,0,canvas.width,canvas.height);
		var data = JSON.stringify(imgData);
		var asd =$('#signatureString');
		asd.val(data);
	}

	function signatureLoad(data){
	    var preParsedData = JSON.parse(data);
		var fin = new Uint8ClampedArray(4*canvas.width*canvas.height);
		for (var i = 0; i < fin.length; i++){
			fin[i]=preParsedData.data[i];
		}    
	    var parsedData = new ImageData(fin,canvas.width,canvas.height);
	    console.log(parsedData);
		ctx.putImageData(parsedData,0,0);
	}
	});
	