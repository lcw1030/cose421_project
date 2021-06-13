"use strict"

var gl, vao, ebo, program;
var sensors = new Float32Array([
    // (x, y, z, force)
    -0.5,  0.5, 0.0, 10.0,
     0.5,  0.5, 0.0, 10.0,
    -0.5, -0.5, 0.0, 10.0,
     0.5, -0.5, 0.0, 10.0,
]);
var sensorIdx;

var postureImages;

var timeline;
var timelineButton;
var timelineStatus = true;

var passiveMode = false;
var lastClickedElem = null;

window.addEventListener("load", function setupWebGL(event) {
    window.removeEventListener(event.type, setupWebGL, false);
 
    postureImages =  [
        document.getElementById('normal'),
        document.getElementById('sway_back'),
        document.getElementById('hunched_back'),
        document.getElementById('legs_crossed'),
        document.getElementById('tilted_body')
    ];

    timeline = document.getElementById('timeline');

    timelineButton = document.querySelector("button");

    var paragraph = document.querySelector("p");
    var canvas = document.querySelector("canvas");

    gl = canvas.getContext("webgl2");
    if (!gl) {
        paragraph.innerHTML = "Failed to get WebGL context";
        return;
    }

    // Create and compile vertex shader
    var source = document.querySelector("#vertex-shader").innerHTML;
    var vertexShader = gl.createShader(gl.VERTEX_SHADER);
    gl.shaderSource(vertexShader, source);
    gl.compileShader(vertexShader);
    var success = gl.getShaderParameter(vertexShader, gl.COMPILE_STATUS);
    if (!success) {
        paragraph.innerHTML = "VS Error: " + gl.getShaderInfoLog(vertexShader);
        return;
    }

    // Create and compile fragment shader
    source = document.querySelector("#fragment-shader").innerHTML;
    var fragmentShader = gl.createShader(gl.FRAGMENT_SHADER);
    gl.shaderSource(fragmentShader, source);
    gl.compileShader(fragmentShader);
    var success = gl.getShaderParameter(fragmentShader, gl.COMPILE_STATUS);
    if (!success) {
        paragraph.innerHTML = "FS Error: " + gl.getShaderInfoLog(fragmentShader);
        return;
    }

    // Create and link program
    var program = gl.createProgram();
    gl.attachShader(program, vertexShader);
    gl.attachShader(program, fragmentShader);
    gl.linkProgram(program);
    gl.detachShader(program, vertexShader);
    gl.detachShader(program, fragmentShader);
    gl.deleteShader(vertexShader);
    gl.deleteShader(fragmentShader);
    success = gl.getProgramParameter(program, gl.LINK_STATUS);
    if (!success) {
        paragraph.innerHTML = "PG Error: " + gl.getProgramInfoLog(program);
        return;
    }

    // Create VAO and initialize attribute
    var vertices = new Float32Array([
        // Triangle 1
         1.0,  1.0,  0.0,
         1.0, -1.0,  0.0,
        -1.0,  1.0,  0.0,
        // Triangle 2
         1.0, -1.0,  0.0,
        -1.0, -1.0,  0.0,
        -1.0,  1.0,  0.0,
    ]);

    var posIdx = gl.getAttribLocation(program, 'position');
    sensorIdx = gl.getUniformLocation(program, 'sensors');

    gl.enableVertexAttribArray(posIdx);
    vao = gl.createBuffer();
    gl.bindBuffer(gl.ARRAY_BUFFER, vao);
    gl.bufferData(gl.ARRAY_BUFFER, vertices, gl.STATIC_DRAW);
    gl.vertexAttribPointer(posIdx, 3, gl.FLOAT, false, 3 * 4, 0);

    gl.viewport(0, 0, gl.drawingBufferWidth, gl.drawingBufferHeight);
    gl.useProgram(program);
    draw(sensorIdx);
}, false);

window.addEventListener("beforeunload", cleanup, true);

function cleanup() {
    gl.useProgram(null);
    if (vao)
        gl.deleteBuffer(vao);
    if (ebo)
        gl.deleteBuffer(ebo);
    if (program)
        gl.deleteProgram(program);
}

function update(data, force = false) {
    if (!passiveMode || force)
    {
        for (let i = 0; i < 4; ++i)
        {
            sensors[i*4+3] = data.sensor[i];
        }
    
        if (data.pos != -1) {
            postureImages.forEach(e => {
                e.classList.add("disabled");
            })
            postureImages[data.pos].classList.remove("disabled");
        }
    }

    if (timelineStatus) {
        let timeElem = document.createElement('div');
        timeElem.setAttribute("data", JSON.stringify(data));
        timeElem.classList.add('time_elem');
        timeElem.innerHTML = data.score;
        timeElem.onclick = () => {
            if (timeElem.classList.contains('clicked'))
            {
                lastClickedElem = null;
                passiveMode = false;
                timeElem.classList.remove('clicked');
            }
            else
            {
                if (lastClickedElem != null) {
                    lastClickedElem.classList.remove('clicked');
                }
                lastClickedElem = timeElem;
                passiveMode = true;
                timeElem.classList.add('clicked');

                update(JSON.parse(timeElem.getAttribute('data')), true);
            }
        };

        if (data.score < 20) {
            timeElem.style.backgroundColor = "red";
        }
        else if (data.score < 70) {
            timeElem.style.backgroundColor = "yellow";
        }
        else {
            timeElem.style.backgroundColor = "LawnGreen";
        }

        timeline.appendChild(timeElem);
    }
}

function toggleTimeline() {
    timelineStatus = !timelineStatus;
    if (!timelineStatus) {
        timelineButton.innerHTML = "Start";
    }
    else {
        timelineButton.innerHTML = "Pause";
    }
}

function draw()
{
    gl.clearColor(0.0, 0.5, 0.0, 1.0);
    gl.clear(gl.COLOR_BUFFER_BIT);

    gl.uniformMatrix4fv(sensorIdx, false, sensors);
    gl.drawArrays(gl.TRIANGLES, 0, 6);
}