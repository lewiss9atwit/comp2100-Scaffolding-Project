from http.server import ThreadingHTTPServer, BaseHTTPRequestHandler
import os
import uuid

UPLOAD_DIR = "uploads"

# Server file for python upload server
class UploadHandler(BaseHTTPRequestHandler):

    def do_POST(self):
        content_type = self.headers.get("Content-Type", "")
        content_length = int(self.headers.get("Content-Length", 0))
        body = self.rfile.read(content_length)

        # Ensure upload directory exists
        os.makedirs(UPLOAD_DIR, exist_ok=True)

        if "multipart/form-data" in content_type:
            boundary = content_type.split("boundary=")[-1].encode()

            # Split into parts
            parts = body.split(b"--" + boundary)
            for part in parts:
                if b"Content-Disposition" in part:
                    # Extract filename if it exists
                    header, filedata = part.split(b"\r\n\r\n", 1)
                    filedata = filedata.rstrip(b"\r\n--")

                    # Attempts to extract the original filename
                    filename = "upload_" + str(uuid.uuid4()) + ".bin"
                    for line in header.split(b"\r\n"):
                        if b"filename=" in line:
                            original = line.split(b"filename=")[1].strip(b"\"")
                            if original:
                                ext = os.path.splitext(original.decode())[1]
                                filename = str(uuid.uuid4()) + ext

                    # Saves file
                    filepath = os.path.join(UPLOAD_DIR, filename)
                    with open(filepath, "wb") as f:
                        f.write(filedata)

        else:
            # Fallback: save raw body
            filename = str(uuid.uuid4()) + ".bin"
            filepath = os.path.join(UPLOAD_DIR, filename)
            with open(filepath, "wb") as f:
                f.write(body)

        # Send response status code is 200
        self.send_response(200)
        self.end_headers()
        self.wfile.write(b"OK")

if __name__ == "__main__":
    server = ThreadingHTTPServer(("0.0.0.0", 8000), UploadHandler)
    print("Upload server running on port 8000 (multipart supported)...")
    server.serve_forever()
