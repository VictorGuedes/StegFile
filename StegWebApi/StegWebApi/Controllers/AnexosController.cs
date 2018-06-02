using System;
using System.Collections.Generic;
using System.Configuration;
using System.Data;
using System.Data.Entity;
using System.Data.Entity.Infrastructure;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Threading.Tasks;
using System.Web;
using System.Web.Hosting;
using System.Web.Http;
using System.Web.Http.Description;
using StegWebApi.Models;

namespace StegWebApi.Controllers
{
    public class AnexosController : ApiController
    {
        private BancoContext db = new BancoContext();

        // GET: api/Anexos
        public IQueryable<Anexo> GetAnexo()
        {
            return db.Anexo;
        }

        // GET: api/Anexos/5
        [ResponseType(typeof(Anexo))]
        public async Task<IHttpActionResult> GetAnexo(int id)
        {
            Anexo anexo = await db.Anexo.FindAsync(id);
            if (anexo == null)
            {
                return NotFound();
            }

            return Ok(anexo);
        }
        // GET: api/Anexos/name
        [Route("api/Anexos/nome/{name}")]
        [HttpGet]
        public async Task<IHttpActionResult>GetByName(string name)
        {
            var anexo = db.Anexo.FirstOrDefault(x => x.NomeArquivo == name);
            
            if (anexo == null)
            {
                return NotFound();
            }
            return Ok(anexo);
        }


        [Route("api/Anexos/download/{link}")]
        [HttpGet]
        public HttpResponseMessage DownloadFile(string link)
        {
            //Anexo anexo = db.Anexo.Find(id);
            Anexo anexo = db.Anexo.FirstOrDefault(x => x.Link == link);
            string fileName = anexo.caminhoArquivo;
            if (!string.IsNullOrEmpty(fileName))
            {
                
                string fullPath = fileName;
                if (File.Exists(fullPath))
                {

                    HttpResponseMessage response = new HttpResponseMessage(HttpStatusCode.OK);
                    var fileStream = new FileStream(fullPath, FileMode.Open);
                    response.Content = new StreamContent(fileStream);
                    response.Content.Headers.ContentType = new MediaTypeHeaderValue("application/octet-stream");
                    response.Content.Headers.ContentDisposition = new ContentDispositionHeaderValue("attachment");
                    response.Content.Headers.ContentDisposition.FileName = fileName;
                    
                    return response;
                }
            }

            return new HttpResponseMessage(HttpStatusCode.NotFound);
        }



        // PUT: api/Anexos/5
        [ResponseType(typeof(void))]
        public async Task<IHttpActionResult> PutAnexo(int id, Anexo anexo)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            if (id != anexo.idAnexo)
            {
                return BadRequest();
            }

            db.Entry(anexo).State = EntityState.Modified;

            try
            {
                await db.SaveChangesAsync();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!AnexoExists(id))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return StatusCode(HttpStatusCode.NoContent);
        }


        [Route("api/upload/{link}")]
        [HttpPost]
        public async Task<HttpResponseMessage> PostAnexo(string link)
        {
            var httpContext = HttpContext.Current;
            string caminhoDiretorio = "~/upload/";
            bool folder = Directory.Exists(HostingEnvironment.MapPath(caminhoDiretorio));

            if (!folder)
            {
                Directory.CreateDirectory(HostingEnvironment.MapPath(caminhoDiretorio));
            }

            // Check for any uploaded file  
            if (httpContext.Request.Files.Count > 0)
            {
                //Loop through uploaded files  
                for (int i = 0; i < httpContext.Request.Files.Count; i++)
                {
                    HttpPostedFile httpPostedFile = httpContext.Request.Files[i];
                    if (httpPostedFile != null)
                    {

                        // Construct file save path  
                        var fileSavePath = Path.Combine(HostingEnvironment.MapPath(caminhoDiretorio), httpPostedFile.FileName);
                        // Save the uploaded file

                        httpPostedFile.SaveAs(fileSavePath);
                        Anexo anexo = new Anexo();
                        anexo.NomeArquivo = httpPostedFile.FileName;
                        anexo.caminhoArquivo = fileSavePath;
                        //anexo.Link = Guid.NewGuid().ToString();
                        anexo.Link = link;
                        db.Anexo.Add(anexo);

                    }
                }
                await db.SaveChangesAsync();
            }

            // Return status code  
            return Request.CreateResponse(HttpStatusCode.Created);
        }



     
        // DELETE: api/Anexos/5
        [ResponseType(typeof(Anexo))]
        public async Task<IHttpActionResult> DeleteAnexo(int id)
        {
            Anexo anexo = await db.Anexo.FindAsync(id);
            if (anexo == null)
            {
                return NotFound();
            }

            db.Anexo.Remove(anexo);
            await db.SaveChangesAsync();

            return Ok(anexo);
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool AnexoExists(int id)
        {
            return db.Anexo.Count(e => e.idAnexo == id) > 0;
        }
    }
}