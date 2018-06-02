namespace StegWebApi.Models
{
    using Newtonsoft.Json;
    using System;
    using System.Collections.Generic;
    using System.ComponentModel.DataAnnotations;
    using System.ComponentModel.DataAnnotations.Schema;
    using System.Data.Entity.Spatial;

    [Table("Usuario")]
    public partial class Usuario
    {
        [Key]
        public int idUsuario { get; set; }

        [Required]
        [StringLength(255)]
        public string username { get; set; }

        [JsonIgnore]
        [Required]
        [StringLength(255)]
        public string password { get; set; }
    }
}
