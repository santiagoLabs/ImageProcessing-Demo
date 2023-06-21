const mixedAlgorithms = [
    "Negative",
    "Rescaling",
    "Shift&Rescale",
    "Bit Plane Slicing",
    "Salt&Pepper noise"
]

const arithmeticOperations = [
    "Addition",
    "Substraction",
    "Multiplication",
    "Division"
]

const bitOperations = [
    "Bitwise Not",
    "Bitwise And",
    "Bitwise Or",
    "Bitwise Xor"
]

const convolutions = [
    "Convolution - Averaging",
    "Convolution - Weighted averaging",
    "Convolution - Four Neighbour Laplacian",
    "Convolution - Eight Neighbour Laplacian",
    "Convolution - Four Neighbour Laplacian Enhancement",
    "Convolution - Eight Neighbour Laplacian Enhancement",
    "Convolution - Roberts One",
    "Convolution - Roberts Two",
    "Convolution - Sobel X",
    "Convolution - Sobel Y"
]

const transformations = [
    "Logarithmic Transformation",
    "Power Law",
    "Random LUT",
]

function buttonsRules(){
    if ($("#originalImage").attr("src") === "") {
        $("#processImage-button").prop("disabled", true);
        $("#saveImage-button").prop("disabled", true);
    }
    else {
        $("#processImage-button").prop("disabled", false);
    }
    if ($("#resultImage").attr("src") !== "") {
        $("#saveImage-button").prop("disabled", false);
    }
}

function setupSelectInputField(){
  var algorithmSelect = $("#algorithmSelect");

  $.each(mixedAlgorithms, function(index, value) {
    var option = $("<option>").text(value).val(value);
    algorithmSelect.append(option);
  });

  $.each(arithmeticOperations, function(index, value) {
    var option = $("<option>").text(value).val(value);
    algorithmSelect.append(option);
  });

  $.each(bitOperations, function(index, value) {
    var option = $("<option>").text(value).val(value);
    algorithmSelect.append(option);
  });

  $.each(convolutions, function(index, value) {
    var option = $("<option>").text(value).val(value);
    algorithmSelect.append(option);
  });

  $.each(transformations, function(index, value) {
    var option = $("<option>").text(value).val(value);
    algorithmSelect.append(option);
  });
}

$(document).ready(function() {
  var formData = new FormData();
  var algorithmSelected;
  var file;
  var file1;
  var urlApiCall;
  
  function algorithmSelection(){
    $("#algorithmSelect").on('change',function(){
      algorithmSelected = $("#algorithmSelect").val();
      if (arithmeticOperations.indexOf(algorithmSelected) !== -1 || bitOperations.indexOf(algorithmSelected) !== -1 && algorithmSelected !== "Bitwise Not"){
        $('#second-image-div').css("display", "contents");
        alert("Upload a second image to use this algorithm!");
      }
      else
        $('#second-image-div').css("display", "none");
    });
  }

  function uploadSecondImage(){
    $("#customFile2").on("change", function() {
      var input = this;
      if (input.files && input.files[0]) {
        file1 = input.files[0];
        if (file1.type.match('image.*')) {
          var reader = new FileReader();
            reader.onload = function(e) {
            var imageDataUrl = e.target.result;
            $("#originalImage2").attr("src", imageDataUrl);
          };
          reader.readAsDataURL(file1);
        } else {
          alert("Invalid file format. Please select an image file.");
        }
      }
    });
  }
  
  function uploadFirstImage(){
    $("#customFile").on("change", function() {
      var input = this;
      if (input.files && input.files[0]) {
          file = input.files[0];
          if (file.type.match('image.*')) {
              var reader = new FileReader();
              reader.onload = function(e) {
                var imageDataUrl = e.target.result;
                $("#originalImage").attr("src", imageDataUrl);
                buttonsRules();
              };
              reader.readAsDataURL(file);
          } else {
              alert("Invalid file format. Please select an image file.");
          }
      }
    });
  }
  
  function processMixedAlgorithms(){
    
      if (algorithmSelected === "Rescaling" || algorithmSelected === "Shift&Rescale"){
        var scalingFactor = prompt("Insert scaling factor")
        if (!$.isNumeric(scalingFactor)){
          alert("Insert a number for the scaling factor!");
          return;
        }
        formData.append("scalingFactor", scalingFactor);
      }
  
      if (algorithmSelected === "Shift&Rescale"){
        var shiftingValue = prompt("Insert shifting value")
        if (!$.isNumeric(shiftingValue)){
          alert("Insert a number for the shifting value!");
          return;
        }
        formData.append("shiftingValue", scalingFactor);
      }

      if (algorithmSelected === "Bit Plane Slicing"){
        var nBit = prompt("Insert number of bit plane")
        if (!$.isNumeric(nBit)){
          alert("Insert a number!");
          return;
        }
        formData.append("nBit", nBit);
      }
      urlApiCall = 'http://localhost:8080/imageProcessing/process';
    
  }
  
  function processArithmeticAlgorithms(){
    
      formData.append("secondImage", file1);
      urlApiCall = 'http://localhost:8080/imageProcessing/process/arithmeticOperations';
    
  }
  
  function processBitOperationsAlgorithms(){
    if (algorithmSelected !== "Bitwise Not")
      formData.append("secondImage", file1);
    
    urlApiCall = 'http://localhost:8080/imageProcessing/process/bitOperations';
  }
  
  function processConvolutions(){
  
    
      urlApiCall = 'http://localhost:8080/imageProcessing/process/convolution';
  }
  
  function processTransformations(){
   
    
      var parameter = prompt("Insert parameter")
      if (!$.isNumeric(parameter)){
        alert("Insert a number!");
        return;
      }
      formData.append("param", parameter);
      urlApiCall = 'http://localhost:8080/imageProcessing/process/transformations';
    
  }
    
  setupSelectInputField();
  buttonsRules();
  algorithmSelection();
  uploadFirstImage();
  uploadSecondImage();

  $("#processImage-button").on("click", function() {
        formData.append("image", file);
        formData.append("algorithm", algorithmSelected);

        if (mixedAlgorithms.indexOf(algorithmSelected) !== -1)
          processMixedAlgorithms();
        
        if (arithmeticOperations.indexOf(algorithmSelected) !== -1)
          processArithmeticAlgorithms(); 
        
        if (bitOperations.indexOf(algorithmSelected) !== -1)
          processBitOperationsAlgorithms();
        
        if (convolutions.indexOf(algorithmSelected) !== -1)
          processConvolutions();
        
        if (transformations.indexOf(algorithmSelected) !== -1)
          processTransformations(); 
        
    
        $.ajax({
            url: urlApiCall,
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function(response) {
                $("#resultImage").attr("src", "data:image/png;base64," + response);
                buttonsRules();
                formData = new FormData();
            },
            error: function(xhr, status, error) {
              alert("Error: ", xhr);
              formData = new FormData();
            }
          });
    });
});

