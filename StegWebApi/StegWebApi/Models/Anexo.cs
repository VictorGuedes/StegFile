namespace StegWebApi.Models
{
    using System;
    using System.Collections.Generic;
    using System.ComponentModel.DataAnnotations;
    using System.ComponentModel.DataAnnotations.Schema;
    using System.Data.Entity.Spatial;

    [Table("Anexo")]
    public partial class Anexo
    {
        [Key]
        public int idAnexo { get; set; }

        [StringLength(255)]
        public string NomeArquivo { get; set; }

        [StringLength(255)]
        public string Link { get; set; }

        [Required]
        [StringLength(255)]
        public string caminhoArquivo { get; set; }
    }
}
